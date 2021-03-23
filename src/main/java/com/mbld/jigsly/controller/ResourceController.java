package com.mbld.jigsly.controller;

import com.mbld.jigsly.constant.ImageConstant;
import com.mbld.jigsly.service.PuzzleImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;

@Controller
public class ResourceController {

    private final PuzzleImageService puzzleImageService;


    public ResourceController(PuzzleImageService puzzleImageService) {
        this.puzzleImageService = puzzleImageService;
    }

    @GetMapping("/resources/images/default-images")
    public ResponseEntity<String[]> getImages(){

        return ResponseEntity.ok(puzzleImageService.getDefaultImages().toArray(new String[0]));
    }
}
