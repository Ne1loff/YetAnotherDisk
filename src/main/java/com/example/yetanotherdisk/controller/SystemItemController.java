package com.example.yetanotherdisk.controller;

import com.example.yetanotherdisk.dto.SystemItemHistoryResponse;
import com.example.yetanotherdisk.dto.SystemItemImportRequest;
import com.example.yetanotherdisk.dto.SystemItemResponse;
import com.example.yetanotherdisk.service.SystemItemServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class SystemItemController {

    private final SystemItemServiceImpl service;

    public SystemItemController(SystemItemServiceImpl service) {
        this.service = service;
    }

    @PostMapping("/imports")
    public ResponseEntity<HttpStatus> importItems(@RequestBody SystemItemImportRequest request) {
        service.importItems(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{id}")
    ResponseEntity<HttpStatus> deleteItem(@PathVariable String id, @RequestParam String date) {
        service.deleteItem(id, date);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/nodes/{id}")
    ResponseEntity<SystemItemResponse> getShopUnitInfo(@PathVariable String id) {
        return ResponseEntity.ok(service.getItem(id));
    }

    @GetMapping("/updates")
    ResponseEntity<SystemItemHistoryResponse> getSales(@RequestParam String date) {
        return ResponseEntity.ok(service.getAllHistoryForLastDay(date));
    }

    @GetMapping("/node/{id}/history")
    ResponseEntity<SystemItemHistoryResponse> getShopUnitStatistic(@PathVariable String id,
                                                                   @RequestParam String dateStart,
                                                                   @RequestParam(defaultValue = "") String dateEnd
    ) {
        return ResponseEntity.ok(service.getItemHistoryBetween(id, dateStart, dateEnd));
    }
}
