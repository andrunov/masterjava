package ru.javaops.masterjava.persist.model;

import com.bertoncelj.jdbi.entitymapper.Column;
import lombok.*;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Project extends BaseEntity {
    @Column("project_name")
    private @NonNull String projectName;

    public Project(Integer id, String projectName) {
        this(projectName);
        this.id = id;
    }
}
