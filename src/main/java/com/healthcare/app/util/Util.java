package com.healthcare.app.util;

import com.health.util.event.UserEvent;
import com.healthcare.app.model.User;
import lombok.extern.log4j.Log4j2;

/**
 * Utility class containing mapping methods for converting between application models and event models.
 */
@Log4j2
public class Util {

    /**
     * Maps the application User model to the event User model for Kafka messaging.
     *
     * @param appUser The application User model to map
     * @return The event User model for Kafka messaging
     */
    public static com.health.util.event.User mapToEventUser(User appUser) {
        if (appUser == null) {
            return null;
        }

        return new com.health.util.event.User(
                appUser.id(),
                appUser.name(),
                appUser.username(),
                appUser.email(),
                mapToEventAddress(appUser.address()),
                appUser.phone(),
                appUser.website(),
                mapToEventCompany(appUser.company())
        );
    }

    /**
     * Maps the application Address model to the event Address model for Kafka messaging.
     *
     * @param appAddress The application Address model to map
     * @return The event Address model for Kafka messaging
     */
    public static com.health.util.event.User.Address mapToEventAddress(User.Address appAddress) {
        if (appAddress == null) {
            return null;
        }

        return new com.health.util.event.User.Address(
                appAddress.street(),
                appAddress.suite(),
                appAddress.city(),
                appAddress.zipcode(),
                mapToEventGeo(appAddress.geo())
        );
    }

    /**
     * Maps the application Geo model to the event Geo model for Kafka messaging.
     *
     * @param appGeo The application Geo model to map
     * @return The event Geo model for Kafka messaging
     */
    public static com.health.util.event.User.Geo mapToEventGeo(User.Geo appGeo) {
        if (appGeo == null) {
            return null;
        }

        return new com.health.util.event.User.Geo(
                appGeo.lat(),
                appGeo.lng()
        );
    }

    /**
     * Maps the application Company model to the event Company model for Kafka messaging.
     *
     * @param appCompany The application Company model to map
     * @return The event Company model for Kafka messaging
     */
    public static com.health.util.event.User.Company mapToEventCompany(User.Company appCompany) {
        if (appCompany == null) {
            return null;
        }

        return new com.health.util.event.User.Company(
                appCompany.name(),
                appCompany.catchPhrase(),
                appCompany.bs()
        );
    }

    /**
     * Creates a UserEvent for the specified event type and user.
     *
     * @param eventType The type of event (e.g., "CREATED", "UPDATED", "DELETED")
     * @param user      The user associated with the event
     * @return A UserEvent instance
     */
    public static UserEvent createUserEvent(String eventType, User user) {
        return new UserEvent(eventType, mapToEventUser(user));
    }
}
