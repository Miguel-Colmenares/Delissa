package com.delissa.controller;

import com.delissa.dto.BoxClosureRequest;
import com.delissa.dto.BoxClosureEditRequest;
import com.delissa.dto.BoxOpeningRequest;
import com.delissa.dto.BoxSummaryResponse;
import com.delissa.model.BoxClosure;
import com.delissa.model.BoxClosureEdit;
import com.delissa.model.BoxOpening;
import com.delissa.service.BoxClosureService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/box-closures")
public class BoxClosureController {

    private final BoxClosureService boxClosureService;

    public BoxClosureController(BoxClosureService boxClosureService) {
        this.boxClosureService = boxClosureService;
    }

    @GetMapping("/summary")
    public BoxSummaryResponse getSummary(@RequestParam(required = false) LocalDate date) {
        return boxClosureService.getSummary(date);
    }

    @PostMapping
    public BoxClosure create(@RequestBody BoxClosureRequest request) {
        return boxClosureService.createClosure(request);
    }

    @PostMapping("/openings")
    public BoxOpening open(@RequestBody BoxOpeningRequest request) {
        return boxClosureService.createOpening(request);
    }

    @GetMapping
    public List<BoxClosure> getAll() {
        return boxClosureService.getClosures();
    }

    @PutMapping("/{id}")
    public BoxClosure edit(@PathVariable Long id, @RequestBody BoxClosureEditRequest request) {
        return boxClosureService.editClosure(id, request);
    }

    @GetMapping("/{id}/edits")
    public List<BoxClosureEdit> getEdits(@PathVariable Long id) {
        return boxClosureService.getClosureEdits(id);
    }
}
