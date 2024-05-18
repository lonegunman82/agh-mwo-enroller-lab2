package com.company.enroller.controllers;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;



    @RestController
    @RequestMapping("/meetings")
    public class MeetingRestController{

        @Autowired
        MeetingService meetingService;
        @Autowired
        ParticipantService participantService;


	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<?> getMeetings() {
		Collection <Meeting> meetingCollection = meetingService.getAll();
		return new ResponseEntity<Collection<Meeting>>(meetingCollection, HttpStatus.OK);
	}


        @RequestMapping(value = "/{id}", method = RequestMethod.GET)
        public ResponseEntity<?> getMeeting(@PathVariable("id") long id) {
            Meeting meeting = meetingService.findById(id);
            if (meeting == null) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
        }


        @RequestMapping(value = "", method = RequestMethod.POST)
        public ResponseEntity<?> addMeeting(@RequestBody Meeting meeting) {
            meetingService.add(meeting);
            return new ResponseEntity<Meeting>(meeting, HttpStatus.CREATED);
        }

        @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
        public ResponseEntity<?> deleteMeeting(@PathVariable("id") long id) {
            Meeting meeting = meetingService.findById(id);
            if (meeting == null) {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
            meetingService.delete(meeting);
            return new ResponseEntity(HttpStatus.OK);
        }


    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateMeeting(@PathVariable("id") long id, @RequestBody Meeting updatedMeeting) {
        Meeting meeting = meetingService.findById(id);
        if (meeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        meeting.setTitle(updatedMeeting.getTitle());
        meeting.setDescription(updatedMeeting.getDescription());
        meeting.setDate(updatedMeeting.getDate());
        meetingService.update(meeting);
        return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
    }

        //curl -X GET http://localhost:8080/meetings/3/participants
        @RequestMapping(value = "/{id}/participants", method = RequestMethod.GET)
        public ResponseEntity<?> getMeetingParticipants(@PathVariable("id") long id) {
            Meeting meeting = meetingService.findById(id);
            if (meeting == null) {
                return new ResponseEntity<>("Spotkanie o podanym ID nie istnieje", HttpStatus.NOT_FOUND);
            }
            Collection<Participant> participants = meeting.getParticipants();
            return new ResponseEntity<>(participants, HttpStatus.OK);
        }

        //curl -X POST -H "Content-Type: application/json" -d '["user2"]' http://localhost:8080/meetings/3/participants
            @RequestMapping(value = "/{id}/participants", method = RequestMethod.POST)
            public ResponseEntity<?> addParticipantsToMeeting(@PathVariable("id") long id, @RequestBody Collection<String> logins) {
                Meeting meeting = meetingService.findById(id);
                if (meeting == null) {
                    return new ResponseEntity<>("Nie ma spotkania o podanym ID", HttpStatus.NOT_FOUND);
                }

                if (logins.isEmpty()) {
                    return new ResponseEntity<>("Lista userów jest pusta", HttpStatus.BAD_REQUEST);
                }

                for (String login : logins) {
                    Participant participant = participantService.findByLogin(login);
                    if (participant == null) {
                        return new ResponseEntity<>("Uczestnik o login " + login + " nie istnieje", HttpStatus.NOT_FOUND);
                    }
                    meeting.addParticipant(participant);
                }
                meetingService.update(meeting);
                return new ResponseEntity<>(meeting, HttpStatus.OK);
            }

        //curl -X DELETE http://localhost:8080/meetings/3/participants/user2

            @RequestMapping(value = "/{meetingId}/participants/{login}", method = RequestMethod.DELETE)
        public ResponseEntity<?> removeParticipantFromMeeting(@PathVariable("meetingId") long meetingId, @PathVariable("login") String login) {
            Meeting meeting = meetingService.findById(meetingId);
            if (meeting == null) {
                return new ResponseEntity<>("Spotkanie o podanym ID nie istnieje", HttpStatus.NOT_FOUND);
            }
            Participant participant = participantService.findByLogin(login);

            if (participant == null) {
                return new ResponseEntity<>("Uczestnik o loginie " + login + " nie istnieje", HttpStatus.NOT_FOUND);

            }
            if (!meeting.getParticipants().contains(participant)) {
                return new ResponseEntity<>("Uczestnik o loginie " + login + " nie jest uczestnikiem tego spotkania", HttpStatus.BAD_REQUEST);

                }
            meeting.removeParticipant(participant);
            meetingService.update(meeting);
            return new ResponseEntity<>(meeting, HttpStatus.OK);
        }

    }



