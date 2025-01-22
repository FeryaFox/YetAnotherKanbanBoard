package ru.feryafox.yetanotherkanbanboard.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.feryafox.yetanotherkanbanboard.models.card.CreateCardDto;
import ru.feryafox.yetanotherkanbanboard.models.card.UpdateCardDto;
import ru.feryafox.yetanotherkanbanboard.services.CardService;

@RestController
@RequestMapping("/card")
public class CardController {
    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

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

    // TODO добавить перемещение карт

}
