package com.example.yetanotherdisk.repository;

import com.example.yetanotherdisk.entity.SystemItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface SystemItemRepository extends JpaRepository<SystemItem, String> {


    Optional<SystemItem> findSystemItemById(@NotNull String id);

    @Query(value = "SELECT item FROM SystemItem item WHERE item.id IN :ids")
    Set<SystemItem> findSystemItemsByIds(@NotNull @Param("ids") Collection<String> ids);


    @Modifying(clearAutomatically = true)
    @Query(value = """
            UPDATE system_item SET size = size + :sizeDiff, date = :date WHERE id IN :ids
            """, nativeQuery = true)
    void updateSizeAndDateById(
            @NotNull @Param("sizeDiff") Long diff,
            @NotNull @Param("date") ZonedDateTime date,
            @Param("ids") Collection<String> ids
    );

    @Query(value = """
            WITH RECURSIVE get_parents(id) AS (
                SELECT id, parent_id FROM system_item WHERE id = :id
                UNION
                SELECT si.id, si.parent_id FROM system_item si
                JOIN get_parents gp ON gp.parent_id = si.id
            )
            SELECT id FROM get_parents
            """, nativeQuery = true)
    List<String> getParentsIdByItemId(@NotNull @Param("id") String id);

}
