package com.lgvt.user_service.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lgvt.user_service.entity.Voter;
import com.lgvt.user_service.service.VoterService;

@RestController
@RequestMapping("/api/auth")
public class VoterRestController {
    private VoterService voterService;

    @Autowired
    public VoterRestController(VoterService voterService) {
        this.voterService = voterService;
    }

    @PostMapping("/voter/register")
    public Voter save(@RequestBody Voter voter) {
        return voterService.saveVoter(voter);
    }
}
