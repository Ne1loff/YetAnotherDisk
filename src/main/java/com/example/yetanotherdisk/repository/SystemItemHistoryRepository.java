package com.example.yetanotherdisk.repository;

import com.example.yetanotherdisk.attributes.SystemItemType;
import com.example.yetanotherdisk.entity.SystemItemHistoryUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;

public interface SystemItemHistoryRepository extends JpaRepository<SystemItemHistoryUnit, Long> {

    List<SystemItemHistoryUnit> getShopHistoriesByTypeAndDateBetween(SystemItemType type, ZonedDateTime after, ZonedDateTime before);

    @Modifying(flushAutomatically = true)
    @Query(value = "UPDATE SystemItemHistoryUnit SET isDeleted = true WHERE itemId IN :ids")
    void markAsDeleted(@Param("ids") Collection<String> ids);

    @Query(value = "SELECT hu FROM SystemItemHistoryUnit hu WHERE hu.itemId = :id AND hu.isDeleted = false AND hu.date >= :dateStart AND hu.date < :dateEnd")
    List<SystemItemHistoryUnit> getShopHistoriesByShopUnitIdAndIsNotDeletedAndDateBetween(
            @Param("id") String id, @Param("dateStart") ZonedDateTime dateStart, @Param("dateEnd") ZonedDateTime dateEnd);

}
