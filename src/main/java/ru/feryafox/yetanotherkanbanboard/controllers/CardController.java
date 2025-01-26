package ru.feryafox.yetanotherkanbanboard.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.feryafox.yetanotherkanbanboard.models.card.*;
import ru.feryafox.yetanotherkanbanboard.services.CardService;

@RestController
@RequestMapping("/card")
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;

    @PostMapping("/")
    public ResponseEntity<?> createCard(@RequestBody CreateCardDto cardDto, @AuthenticationPrincipal UserDetails userDetails) {
        Long id = cardService.createCard(cardDto, userDetails.getUsername());
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateCard(@RequestBody UpdateCardDto cardDto, @PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        cardService.updateCard(id, cardDto, userDetails.getUsername());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{id}/move")
    public ResponseEntity<?> moveCard(@RequestBody MoveCardDto moveCardDto, @PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails){
        cardService.moveCard(id, moveCardDto, userDetails.getUsername());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCard(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        cardService.deleteCard(id, userDetails.getUsername());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{id}/responsible")
    public ResponseEntity<?> responsibleCard(@RequestBody SetResponsibleDto setResponsibleDto, @PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        ResponsibleUserDto responsibleUserDto = cardService.setResponsible(setResponsibleDto.getUsername(), userDetails.getUsername(), id);
        return new ResponseEntity<>(responsibleUserDto, HttpStatus.CREATED);
    }
}