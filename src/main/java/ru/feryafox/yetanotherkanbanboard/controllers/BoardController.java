package ru.feryafox.yetanotherkanbanboard.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.feryafox.yetanotherkanbanboard.entities.Board;
import ru.feryafox.yetanotherkanbanboard.models.board.BoardInfoDto;
import ru.feryafox.yetanotherkanbanboard.models.board.CreateBoardDto;
import ru.feryafox.yetanotherkanbanboard.models.board.UpdateBoardTitleDto;
import ru.feryafox.yetanotherkanbanboard.services.BoardService;

@RestController
@RequestMapping("/board")
public class BoardController {
    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping("/{id}") // TODO добавить Swagger Аннотации
    public BoardInfoDto getBoard(@Parameter(description = "Id доски") @PathVariable long id, @AuthenticationPrincipal UserDetails userDetails) {
        return boardService.getBoardInfo(id, userDetails);
    }

   @PostMapping("/")// TODO добавить Swagger Аннотации
   public ResponseEntity<?> createBoard(@AuthenticationPrincipal UserDetails userDetails, @RequestBody CreateBoardDto createBoardDto) {
        long boardId = boardService.createBoard(createBoardDto, userDetails);

        return new ResponseEntity<>(boardId, HttpStatus.CREATED);
   }

   @PatchMapping("/{id}")
   public ResponseEntity<?> updateBoardTitle(@AuthenticationPrincipal UserDetails userDetails, @PathVariable long id, @RequestBody UpdateBoardTitleDto updateBoardTitleDto) {
        boardService.updateBoardTitle(id, updateBoardTitleDto.getTitle(), userDetails);
        return ResponseEntity.noContent().build();
   }
}
