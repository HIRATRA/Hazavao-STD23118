package hei.exam.demo.file.hash;

import hei.exam.demo.PojaGenerated;

@PojaGenerated
public record FileHash(FileHashAlgorithm algorithm, String value) {}
