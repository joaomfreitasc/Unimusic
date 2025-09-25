package br.unibh.sdm.unimusic_music.rest;

import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.file.*;

@RestController
@RequestMapping("/stream")
public class StreamingController {

    private static final String MUSICA_DIR = "C:\\Users\\junii\\uploads\\"; // pasta onde você salva os mp3

    @GetMapping("/{nomeArquivo}")
    public ResponseEntity<ResourceRegion> streamMusica(
            @PathVariable String nomeArquivo,
            @RequestHeader HttpHeaders headers) throws IOException {

        File arquivo = new File(MUSICA_DIR + nomeArquivo);

        if (!arquivo.exists()) {
            return ResponseEntity.notFound().build();
        }

        // Converte para recurso legível pelo Spring
        UrlResource video = new UrlResource(arquivo.toURI());
        long contentLength = video.contentLength();

        // Pega header Range (ex: bytes=1000-)
        HttpRange httpRange = headers.getRange().isEmpty() ? null : headers.getRange().get(0);

        ResourceRegion region;
        if (httpRange != null) {
            long start = httpRange.getRangeStart(contentLength);
            long end = httpRange.getRangeEnd(contentLength);
            long rangeLength = Math.min(1024 * 1024, end - start + 1); // envia em blocos de até 1MB
            region = new ResourceRegion(video, start, rangeLength);
        } else {
            long rangeLength = Math.min(1024 * 1024, contentLength);
            region = new ResourceRegion(video, 0, rangeLength);
        }

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaTypeFactory.getMediaType(video).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .body(region);
    }
}
