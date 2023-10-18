package ru.yandex.practicum.event.model;

public enum StateAction {
    // user
    SEND_TO_REVIEW,
    CANCEL_REVIEW,

    // admin
    PUBLISH_EVENT,
    REJECT_EVENT
}
