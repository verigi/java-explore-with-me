package ru.practicum.general.enums;

public enum StateAction {
    // admin/event
    PUBLISH_EVENT,
    REJECT_EVENT,

    // admin/comment
    PUBLISH_COMMENT,
    REJECT_COMMENT,

    // user/event
    SEND_TO_REVIEW,
    CANCEL_REVIEW,
}