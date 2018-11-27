package ru.javaops.masterjava.persist.model;

import com.bertoncelj.jdbi.entitymapper.Column;
import lombok.*;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Group extends BaseEntity{
    @Column("group_name")
    private @NonNull String groupName;
    @Column("group_type")
    private @NonNull GroupType groupType;

    public Group(Integer id, String fullName, GroupType groupType) {
        this(fullName, groupType);
        this.id = id;
    }
}
