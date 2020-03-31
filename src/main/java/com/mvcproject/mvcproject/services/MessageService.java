package com.mvcproject.mvcproject.services;

import com.mvcproject.mvcproject.dto.DialogDtoResponse;
import com.mvcproject.mvcproject.dto.InterlocutorDto;
import com.mvcproject.mvcproject.dto.MessageDto;
import com.mvcproject.mvcproject.entities.Dialog;
import com.mvcproject.mvcproject.entities.Message;
import com.mvcproject.mvcproject.entities.ShowStatus;
import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.exceptions.ErrorPageException;
import com.mvcproject.mvcproject.repositories.DialogRepo;
import com.mvcproject.mvcproject.repositories.MessageRepo;
import com.mvcproject.mvcproject.repositories.ShowStatusRepo;
import com.mvcproject.mvcproject.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    @Autowired
    private ShowStatusRepo showStatusRepo;
    @Autowired
    private MessageService messageService;

    @Transactional
    public Set<DialogDtoResponse> getDialogs(User[] userFromDB, Long id) {
        userFromDB[0] = userRepo.findById(id).orElseThrow();
        Set<DialogDtoResponse> response = new LinkedHashSet<>();
        sortedByDescAndFormDialogRtoResponse(userFromDB[0], id, response);
        return response;
    }

    private void sortedByDescAndFormDialogRtoResponse(User user, Long id, Set<DialogDtoResponse> response) {
        user.getDialogs().stream()
                .sorted(Comparator.comparingLong(Dialog::getLastNewMessage).reversed())
                .collect(Collectors.toCollection(LinkedHashSet::new))
                .forEach(dialog -> {
                    if (dialog.getShowStatuses().stream().anyMatch(showStatus ->
                            showStatus.getUsername().equals(user.getUsername()) && showStatus.isVisible())) {
                        dialog.getUsers().forEach(user1 -> {
                            if (!user1.getId().equals(id)) {
                                response.add(new DialogDtoResponse(dialog.getId(), user1.getFirstname(),
                                        user1.getLastname(), user1.getUsername(), dialog.getHaveNewMessages()));
                            }
                        });
                    }
                });
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
        findShowStatusByDialogAndUsernameAndSetVisibility(dialog, toUser, true);
        dialog.setHaveNewMessages(true);
        dialog.setLastNewMessage(System.currentTimeMillis());
        Message message = new Message(null, msg.getText(), new Date(),
                fromUser.getId(), toUser.getId(), dialog, true);
        messageRepo.save(message);
        userRepo.save(toUser);
        dialogRepo.save(dialog);
        return new MessageDto(msg.getFrom(), msg.getTo(), msg.getText(),
                setCurrentDate(message.getDate()));
    }

    private void findShowStatusByDialogAndUsernameAndSetVisibility(Dialog dialog,
                                                                   User userFromDB, boolean visible) {
        dialog.getShowStatuses().stream()
                .filter(showStatus -> showStatus.getUsername().equals(userFromDB.getUsername()))
                .findFirst()
                .ifPresent(showStatus -> showStatus.setVisible(visible));
    }

    @Transactional
    public void readNewMessage(User user, Dialog dialog) {
        List<Message> messages = messageRepo.findByNewMessageAndDialog(true,
                dialog);
        messages.forEach(message -> readNewMessagesIfExist(user, message));
        if (dialogRepo.findDialogsByContainingUserNative(user.getId())
                .stream().noneMatch(Dialog::getHaveNewMessages)) {
            user.setHaveNewMessages(false);
            userRepo.save(user);
        }
        messageRepo.saveAll(messages);
    }

    @Transactional
    public void readNewMessage(User user, Long dialogId) {
        User userFromDB = userRepo.findById(user.getId()).orElseThrow();
        List<Message> messages = messageRepo.findByNewMessageAndDialog(true,
                getDialogFromUserDialogs(userFromDB, dialogId));
        messages.forEach(message -> readNewMessagesIfExist(userFromDB, message));
        if (userFromDB.getDialogs().stream().noneMatch(Dialog::getHaveNewMessages)) {
            userFromDB.setHaveNewMessages(false);
            userRepo.save(userFromDB);
        }
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

    @Transactional
    public void deleteDialog(String strDialogId, User user) {
        User userFromDB = userRepo.findById(user.getId()).orElseThrow();
        Long dialogId = Long.valueOf(strDialogId);
        Dialog dialog = dialogRepo.findById(dialogId).orElseThrow();
        messageService.readNewMessage(userFromDB, dialog);
        findShowStatusByDialogAndUsernameAndSetVisibility(dialog, userFromDB, false);
        if (dialog.getShowStatuses().stream().noneMatch(ShowStatus::isVisible))
            dialogRepo.deleteById(dialogId);
        else
            dialogRepo.save(dialog);
    }

    private Dialog getDialog(User user, long friendId) {
        User principal = userRepo.findById(user.getId()).orElseThrow();
        Optional<Dialog> any = principal.getDialogs().stream().filter(dialog -> dialog.getUsers()
                .stream()
                .anyMatch(user1 -> user1.getId().equals(friendId))).findAny();
        return any.orElse(null);
    }

    @Transactional
    public long determineDialog(long friendId, User user) {
        Dialog sameDialog = getDialog(user, friendId);
        if (sameDialog != null) {
            setShowStatusTrue(user, sameDialog);
        } else {
            return createDialogWithShowStatus(friendId, user).getId();
        }
        return sameDialog.getId();
    }

    private Dialog createDialogWithShowStatus(long friendId, User user) {
        User friendFromDB = userRepo.findById(friendId).orElseThrow();
        Dialog savedDialog = dialogRepo.save(new Dialog(null, Stream.of(user, friendFromDB)
                .collect(Collectors.toSet()), new ArrayList<>(), null,
                false, System.currentTimeMillis()));
        ShowStatus showStatus1 = new ShowStatus(null, user.getUsername(), savedDialog, true);
        ShowStatus showStatus2 = new ShowStatus(null, friendFromDB.getUsername(), savedDialog, false);
        showStatusRepo.save(showStatus1);
        showStatusRepo.save(showStatus2);
        return savedDialog;
    }

    private void setShowStatusTrue(User user, Dialog sameDialog) {
        List<ShowStatus> showStatuses = showStatusRepo.findByDialog(sameDialog);
        ShowStatus showStatusPrincipal = showStatuses
                .stream()
                .filter(showStatus -> showStatus.getUsername().equals(user.getUsername()))
                .findAny()
                .orElseThrow();
        showStatusPrincipal.setVisible(true);
        showStatusRepo.save(showStatusPrincipal);
    }
}
