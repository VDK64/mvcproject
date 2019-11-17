package com.mvcproject.mvcproject.services;

import com.mvcproject.mvcproject.dto.InterlocutorDto;
import com.mvcproject.mvcproject.dto.MessageDto;
import com.mvcproject.mvcproject.dto.DialogDtoResponse;
import com.mvcproject.mvcproject.entities.Dialog;
import com.mvcproject.mvcproject.entities.Message;
import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.repositories.DialogRepo;
import com.mvcproject.mvcproject.repositories.MessageRepo;
import com.mvcproject.mvcproject.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.Principal;
import java.util.*;
import java.util.function.Consumer;

@Service
public class MessageService {
    @Autowired
    private DialogRepo dialogRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private MessageRepo messageRepo;
    @Autowired
    private SimpMessagingTemplate template;

    @Transactional
    public Set<DialogDtoResponse> getDialogs(Long id) {
        Set<DialogDtoResponse> response = new LinkedHashSet<>();
        User user = userRepo.findById(id).orElse(null);
        assert user != null;
        Set<Dialog> dialogs = user.getDialogs();
        for (Dialog dialog : dialogs) {
            for (User dUser : dialog.getUsers()) {
                if (!dUser.getId().equals(id)) {
                    response.add(new DialogDtoResponse(dialog.getId(), dUser.getFirstname(),
                            dUser.getLastname(), dUser.getUsername()));
                }
            }
        }
        return response;
    }

    @Transactional
    public List<MessageDto> loadMessages(User user, Long dialogId) {
        List<MessageDto> response = new ArrayList<>();
        //noinspection OptionalGetWithoutIsPresent
        dialogRepo.findById(dialogId).ifPresent(dialogDB -> dialogDB.getMessages().forEach(
                message -> response.add(new MessageDto(userRepo.findById(message.getFromId()).get().getUsername(),
                        userRepo.findById(message.getToId()).get().getUsername(), message.getText(), message.getDate(),
                        dialogId
        ))));
        return response;
    }

    @Transactional
    public InterlocutorDto getInterlocutor(Long dialogId, Long id) {
        final InterlocutorDto[] interlocutorDto = new InterlocutorDto[1];
        dialogRepo.findById(dialogId).orElseThrow().getUsers().forEach(user -> {
            if (!user.getId().equals(id)) {
                interlocutorDto[0] = new InterlocutorDto(user.getId(), user.getAvatar(), user.getUsername());
            }
        });

        return interlocutorDto[0];
    }

    @Transactional
    public void sendMessage(Principal user, MessageDto msg) {
        User fromUser = userRepo.findByUsername(msg.getFrom()).orElseThrow();
        User toUser = userRepo.findByUsername(msg.getTo()).orElseThrow();
        Dialog dialog = dialogRepo.findById(msg.getDialogId()).orElseThrow();
        Message message = new Message(null, msg.getText(), new Date(), fromUser.getId(), toUser.getId(), dialog);
        messageRepo.save(message);
        MessageDto out = new MessageDto(msg.getFrom(), msg.getTo(), msg.getText(), message.getDate());
        template.convertAndSendToUser(msg.getTo(), "/queue/updates", out);
    }
}
