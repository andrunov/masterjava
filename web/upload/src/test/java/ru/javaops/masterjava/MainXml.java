package ru.javaops.masterjava;

import com.google.common.base.Splitter;
import com.google.common.io.Resources;
import com.typesafe.config.Config;
import j2html.tags.ContainerTag;
import one.util.streamex.StreamEx;
import ru.javaops.masterjava.config.Configs;
import ru.javaops.masterjava.persist.DBIProvider;
import ru.javaops.masterjava.persist.dao.CityDao;
import ru.javaops.masterjava.persist.dao.UserDao;
import ru.javaops.masterjava.persist.model.City;
import ru.javaops.masterjava.persist.model.UserFlag;
import ru.javaops.masterjava.xml.schema.*;
import ru.javaops.masterjava.xml.util.*;

import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.DriverManager;
import java.util.*;

import static com.google.common.base.Strings.nullToEmpty;
import static j2html.TagCreator.*;

public class MainXml {

    private static final Comparator<User> USER_COMPARATOR = Comparator.comparing(User::getValue).thenComparing(User::getEmail);

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Required argument: projectName");
            System.exit(1);
        }
        String projectName = args[0];
        URL payloadUrl = Resources.getResource("payload.xml");

        Set<User> users = parseByJaxb(projectName, payloadUrl);
        users.forEach(System.out::println);

        System.out.println();
        String html = toHtml(users, projectName);
        System.out.println(html);
        try (Writer writer = Files.newBufferedWriter(Paths.get("out/users.html"))) {
            writer.write(html);
        }

        System.out.println();
        users = processByStax(projectName, payloadUrl);
        users.forEach(System.out::println);

        System.out.println();
        html = transform(projectName, payloadUrl);
        try (Writer writer = Files.newBufferedWriter(Paths.get("out/groups.html"))) {
            writer.write(html);
        }
    }

    private static Set<User> parseByJaxb(String projectName, URL payloadUrl) throws Exception {
        JaxbParser parser = new JaxbParser(ObjectFactory.class);
        JaxbUnmarshaller unmarshaller = parser.createUnmarshaller();
        parser.setSchema(Schemas.ofClasspath("payload.xsd"));
        Payload payload;
        try (InputStream is = payloadUrl.openStream()) {
            payload = unmarshaller.unmarshal(is);
        }


        List<CityType> cityTypes = (payload.getCities().getCity());
        List<City> cities = new ArrayList<>();
        for (CityType cityType : cityTypes) {
            City city = new City(cityType.getValue(),cityType.getId());
            cities.add(city);
        }

        Config db = Configs.getConfig("persist.conf","db");
        DBIProvider.init(() -> {
            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("PostgreSQL driver not found", e);
            }
            return DriverManager.getConnection(db.getString("url"), db.getString("user"), db.getString("password"));
        });
        CityDao dao = DBIProvider.getDao(CityDao.class);
        DBIProvider.getDBI().useTransaction((conn, status) -> {
            cities.forEach(dao::insert);
        });

        Project project = StreamEx.of(payload.getProjects().getProject())
                .filter(p -> p.getName().equals(projectName))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Invalid project name '" + projectName + '\''));

        final Set<Project.Group> groups = new HashSet<>(project.getGroup());  // identity compare
        Set<User> users = StreamEx.of(payload.getUsers().getUser())
                .filter(u -> !Collections.disjoint(groups, u.getGroupRefs()))
                .toCollection(() -> new TreeSet<>(USER_COMPARATOR));

        List<ru.javaops.masterjava.persist.model.User> persistUsers = new ArrayList<>();
        for (User user : users) {
            ru.javaops.masterjava.persist.model.User pUser = new ru.javaops.masterjava.persist.model.User(
                                    user.getValue(),
                                    user.getEmail(),
                                    UserFlag.valueOf(user.getFlag().value()),
                                    ((CityType)user.getCity()).getId());
            persistUsers.add(pUser);
        }
        UserDao userDao = DBIProvider.getDao(UserDao.class);
        DBIProvider.getDBI().useTransaction((conn, status) -> {
            persistUsers.forEach(userDao::insert);
        });

        return users;

    }

    private static Set<User> processByStax(String projectName, URL payloadUrl) throws Exception {

        try (InputStream is = payloadUrl.openStream()) {
            StaxStreamProcessor processor = new StaxStreamProcessor(is);
            final Set<String> groupNames = new HashSet<>();

            // Projects loop
            projects:
            while (processor.startElement("Project", "Projects")) {
                if (projectName.equals(processor.getAttribute("name"))) {
                    while (processor.startElement("Group", "Project")) {
                        groupNames.add(processor.getAttribute("name"));
                    }
                    break;
                }
            }
            if (groupNames.isEmpty()) {
                throw new IllegalArgumentException("Invalid " + projectName + " or no groups");
            }

            // Users loop
            Set<User> users = new TreeSet<>(USER_COMPARATOR);

            JaxbParser parser = new JaxbParser(ObjectFactory.class);
            JaxbUnmarshaller unmarshaller = parser.createUnmarshaller();
            while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
                String groupRefs = processor.getAttribute("groupRefs");
                if (!Collections.disjoint(groupNames, Splitter.on(' ').splitToList(nullToEmpty(groupRefs)))) {
                    User user = unmarshaller.unmarshal(processor.getReader(), User.class);
                    users.add(user);
                }
            }
            return users;
        }
    }

    private static String toHtml(Set<User> users, String projectName) {
        final ContainerTag table = table().with(
                tr().with(th("FullName"), th("email")))
                .attr("border", "1")
                .attr("cellpadding", "8")
                .attr("cellspacing", "0");

        users.forEach(u -> table.with(tr().with(td(u.getValue()), td(u.getEmail()))));

        return html().with(
                head().with(title(projectName + " users")),
                body().with(h1(projectName + " users"), table)
        ).render();
    }

    private static String transform(String projectName, URL payloadUrl) throws Exception {
        URL xsl = Resources.getResource("groups.xsl");
        try (InputStream xmlStream = payloadUrl.openStream(); InputStream xslStream = xsl.openStream()) {
            XsltProcessor processor = new XsltProcessor(xslStream);
            processor.setParameter("projectName", projectName);
            return processor.transform(xmlStream);
        }
    }
}
