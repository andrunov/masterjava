package ru.javaops.masterjava.persist.model;

import com.bertoncelj.jdbi.entitymapper.Column;
import lombok.*;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class City extends BaseEntity {
    @Column("city_name")
    private @NonNull String cityName;
    @Column("city_code")
    private @NonNull String cityCode;

    public City(Integer id, String cityName, String cityCode) {
        this(cityName, cityCode);
        this.id=id;
    }
}
