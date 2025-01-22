package ru.feryafox.yetanotherkanbanboard.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.feryafox.yetanotherkanbanboard.entities.Column;
import ru.feryafox.yetanotherkanbanboard.models.column.CreateColumnDto;
import ru.feryafox.yetanotherkanbanboard.models.column.UpdateColumnDto;
import ru.feryafox.yetanotherkanbanboard.services.ColumnService;

@RestController
@RequestMapping("/column")
public class ColumnController {
    private final ColumnService columnService;

    public ColumnController(ColumnService columnService) {
        this.columnService = columnService;
    }

    @PostMapping("/")
    public ResponseEntity<?> createColumn(@RequestBody CreateColumnDto createColumnDto, @AuthenticationPrincipal UserDetails userDetails) {
        Column column = columnService.createColumn(createColumnDto.getColumnTitle(), userDetails.getUsername(), createColumnDto.getBoardId());
        return new ResponseEntity<>(column.getId(), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateColumn(@RequestBody UpdateColumnDto updateColumnDto, @PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        columnService.updateColumn(userDetails.getUsername(), id, updateColumnDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
