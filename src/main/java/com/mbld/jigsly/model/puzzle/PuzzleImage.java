package com.mbld.jigsly.model.puzzle;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PuzzleImage {
    private final String imageBase64;
    private final int width;
    private final int height;
}
