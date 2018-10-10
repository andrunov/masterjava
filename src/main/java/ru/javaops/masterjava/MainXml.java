package ru.javaops.masterjava;

import com.google.common.io.Resources;
import org.xml.sax.SAXException;
import ru.javaops.masterjava.xml.schema.*;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.Schemas;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainXml {
    public static void main(String[] args) {
        showProjectParticipate("Masterjava");

    }

    private static void showProjectParticipate (String projectName) {
        JaxbParser JAXB_PARSER = new JaxbParser(ObjectFactory.class);
        Payload payload = null;
        try {
            payload = JAXB_PARSER.unmarshal(Resources.getResource("payload.xml").openStream());
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (payload == null) return;

        List<User> users = new ArrayList<>();

        for (Project project : payload.getProjects().getProject()) {
            if (project.getName().equals(projectName)) {
                for (Group group : project.getGroups().getGroup()){
                    for (Group.Users.User user : group.getUsers().getUser()){
                       // if (users.contains(user.getUserId()))
                        System.out.println(((User) user.getUserId()).getFullName());
                    }


                }
            }
        }

    }

}
