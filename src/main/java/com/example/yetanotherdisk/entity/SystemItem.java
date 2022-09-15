package com.example.yetanotherdisk.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Accessors(chain = true)
public class SystemItem extends AbstractEntity {

    @Id
    private String id;

    private String parentId;

    private String url;

    @Column(nullable = false)
    private Long size;

    @BatchSize(size = 20)
    @OneToMany(mappedBy = "parentId", fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
    private List<SystemItem> children = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SystemItem that)) return false;
        if (!super.equals(o)) return false;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + id.hashCode();
        return result;
    }
}
