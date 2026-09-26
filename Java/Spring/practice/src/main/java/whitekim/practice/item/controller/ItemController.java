package whitekim.practice.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import whitekim.practice.item.dto.request.RegisterItemForm;
import whitekim.practice.item.dto.response.RespItemInfo;
import whitekim.practice.item.service.ItemService;

@RestController
@RequestMapping("/item")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/info")
    public ResponseEntity<RespItemInfo> getItemInfo(@RequestParam Long itemId) {
        RespItemInfo respItemInfo = itemService.getItemInfo(itemId);

        return ResponseEntity.ok(respItemInfo);
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerItem(@RequestBody RegisterItemForm itemForm) {
        itemService.registerItemInfo(itemForm);

        return ResponseEntity.ok("등록 성공");
    }
}
