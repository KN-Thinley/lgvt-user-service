package com.lgvt.user_service.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties.Http;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    public ResponseEntity<?> save(@RequestPart("voter") String voterJson, @RequestPart MultipartFile file) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Voter voter = objectMapper.readValue(voterJson, Voter.class);
            Voter voter_Res = voterService.saveVoter(voter, file);

            System.out.println("Voter: " + voterJson);
            System.out.println("file: " + file.getOriginalFilename());
            return new ResponseEntity<>(voter_Res, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/voter/login")
    public Voter login(@RequestBody Voter voter) {
        return null;
    }
}
