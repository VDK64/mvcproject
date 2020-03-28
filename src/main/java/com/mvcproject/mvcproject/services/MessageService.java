package com.mvcproject.mvcproject.services;

import com.mvcproject.mvcproject.dto.DialogDtoResponse;
import com.mvcproject.mvcproject.dto.InterlocutorDto;
import com.mvcproject.mvcproject.dto.MessageDto;
import com.mvcproject.mvcproject.entities.Dialog;
import com.mvcproject.mvcproject.entities.Message;
import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.exceptions.ErrorPageException;
import com.mvcproject.mvcproject.repositories.DialogRepo;
import com.mvcproject.mvcproject.repositories.MessageRepo;
import com.mvcproject.mvcproject.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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
    public Set<DialogDtoResponse> getDialogs(User[] userFromDB, Long id) {
        Set<DialogDtoResponse> response = new LinkedHashSet<>();
        userFromDB[0] = userRepo.findById(id).orElse(null);
        assert userFromDB[0] != null;
        sortedByDescAndFormDialogRtoResponse(userFromDB[0], id, response);
        return response;
    }

    private void sortedByDescAndFormDialogRtoResponse(User user, Long id, Set<DialogDtoResponse> response) {
        user.getDialogs().stream()
                .sorted(Comparator.comparingLong(Dialog::getLastNewMessage).reversed())
                .collect(Collectors.toCollection(LinkedHashSet::new))
                .forEach(dialog -> dialog.getUsers().forEach(user1 -> {
                    if (!user1.getId().equals(id)) {
                        response.add(new DialogDtoResponse(dialog.getId(), user1.getFirstname(),
                                user1.getLastname(), user1.getUsername(), dialog.getHaveNewMessages()));
                    }
                }));
    }

    private void loadMessages(Dialog dialog, List<MessageDto> messageList, User user) {
        String interlocutorUsername = getInterlocutor(dialog, user.getId()).getUsername();
        dialog.getMessages().forEach(
                message -> messageList.add(new MessageDto(usernameFromIdInDialog(user, interlocutorUsername,
                        message.getFromId()), usernameFromIdInDialog(user, interlocutorUsername, message.getToId()),
                        message.getText(), setCurrentDate(message.getDate()), dialog.getId())));
    }

    public InterlocutorDto getInterlocutor(Dialog dialog, Long id) {
        final InterlocutorDto[] interlocutorDto = new InterlocutorDto[1];
        dialog.getUsers().forEach(user -> {
            if (!user.getId().equals(id)) {
                interlocutorDto[0] = new InterlocutorDto(user.getId(),
                        user.getAvatar(), user.getUsername());
            }
        });
        return interlocutorDto[0];
    }

    private String setCurrentDate(Date date) {
        String pattern = "MM.dd.yyyy, HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(date);
    }

    @Transactional
    public MessageDto sendMessage(MessageDto msg) {
        User fromUser = userRepo.findByUsername(msg.getFrom()).orElseThrow();
        User toUser = userRepo.findByUsername(msg.getTo()).orElseThrow();
        toUser.setHaveNewMessages(true);
        Dialog dialog = dialogRepo.findById(msg.getDialogId()).orElseThrow();
        dialog.setHaveNewMessages(true);
        dialog.setLastNewMessage(System.currentTimeMillis());
        Message message = new Message(null, msg.getText(), new Date(),
                fromUser.getId(), toUser.getId(), dialog, true);
        messageRepo.save(message);
        userRepo.save(toUser);
        return new MessageDto(msg.getFrom(), msg.getTo(), msg.getText(),
                setCurrentDate(message.getDate()));
    }

    @Transactional
    public void readNewMessage(User user, Dialog dialog) {
        List<Message> messages = messageRepo.findByNewMessageAndDialog(true,
                dialog);
        messages.forEach(message -> readNewMessagesIfExist(user, message));
        user.setHaveNewMessages(false);
        userRepo.save(user);
        messageRepo.saveAll(messages);
    }

    @Transactional
    public void readNewMessage(User user, Long dialogId) {
        User userFromDB = userRepo.findById(user.getId()).orElseThrow();
        List<Message> messages = messageRepo.findByNewMessageAndDialog(true,
                getDialogFromUserDialogs(userFromDB, dialogId));
        messages.forEach(message -> readNewMessagesIfExist(userFromDB, message));
        userFromDB.setHaveNewMessages(false);
        userRepo.save(userFromDB);
        messageRepo.saveAll(messages);
    }

    private void readNewMessagesIfExist(User user, Message message) {
        if (message.getToId().equals(user.getId())) {
            message.setNewMessage(false);
            message.getDialog().setHaveNewMessages(false);
        }
    }

    private Dialog getDialogFromUserDialogs(User user, long dialogId) {
        return user.getDialogs().stream().filter(dialog -> dialog.getId()
                .equals(dialogId)).findFirst().orElseThrow();
    }

    private String usernameFromIdInDialog(User user, String interUsername, long id) {
        if (id == user.getId())
            return user.getUsername();
        else
            return interUsername;
    }

    @Transactional
    public Dialog accessRouter(List<MessageDto> messageList, User user,
                               Long dialogId, ModelAndView model) {
        Dialog dialog = dialogRepo.findById(dialogId).orElseThrow(() -> {
            model.setViewName("errorPage");
            model.addObject("newMessages", user.isHaveNewMessages());
            throw new ErrorPageException(model);
        });
        dialog.getUsers().stream()
                .filter(user1 -> user1.getId().equals(user.getId()))
                .findFirst().orElseThrow(() -> {
            model.setViewName("errorPage");
            model.addObject("newMessages", user.isHaveNewMessages());
            throw new ErrorPageException(model);
        });
        loadMessages(dialog, messageList, user);
        return dialog;
    }
}
