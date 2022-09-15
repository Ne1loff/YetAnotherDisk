package com.example.yetanotherdisk.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Accessors(chain = true)
@Table(name = "system_item_history")
public class SystemItemHistoryUnit extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "history_seq")
    @SequenceGenerator(name = "history_seq", sequenceName = "history_sequence", allocationSize = 20)
    private Long id;

    @Column(nullable = false)
    private String itemId;
    private String url;
    private String parentId;
    private boolean isDeleted;
    private Long size;

    @Override
    @Column(nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    public ZonedDateTime getDate() {
        return super.getDate();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SystemItemHistoryUnit that)) return false;
        if (!super.equals(o)) return false;

        if (!id.equals(that.id)) return false;
        return itemId.equals(that.itemId);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + id.hashCode();
        result = 31 * result + itemId.hashCode();
        return result;
    }
}
