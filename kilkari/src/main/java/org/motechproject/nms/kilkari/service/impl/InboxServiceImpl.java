package org.motechproject.nms.kilkari.service.impl;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.motechproject.nms.kilkari.domain.InboxCallDetails;
import org.motechproject.nms.kilkari.domain.Subscription;
import org.motechproject.nms.kilkari.domain.SubscriptionPackMessage;
import org.motechproject.nms.kilkari.domain.SubscriptionStatus;
import org.motechproject.nms.kilkari.repository.InboxCallDetailsDataService;
import org.motechproject.nms.kilkari.service.InboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service("inboxService")
public class InboxServiceImpl implements InboxService {

    private static final int DAYS_IN_WEEK = 7;

    private InboxCallDetailsDataService inboxCallDetailsDataService;

    @Autowired
    public InboxServiceImpl(InboxCallDetailsDataService inboxCallDetailsDataService) {
        this.inboxCallDetailsDataService = inboxCallDetailsDataService;
    }

    @Override
    public long addInboxCallDetails(InboxCallDetails inboxCallDetails) {
        InboxCallDetails newRecord = inboxCallDetailsDataService.create(inboxCallDetails);
        return (long) inboxCallDetailsDataService.getDetachedField(newRecord, "id");
    }

    @Override
    public SubscriptionPackMessage getInboxMessage(Subscription subscription) {

        if (subscription.getStartDate() == null || subscription.getStartDate().isAfter(LocalDate.now())) {
            return null;
        }
        if (subscription.getStatus() == SubscriptionStatus.DEACTIVATED ||
                subscription.getStatus() == SubscriptionStatus.PENDING_ACTIVATION) {
            return null;
        }
        if (subscription.getStatus() == SubscriptionStatus.COMPLETED) {
            // TODO: if <= 7 days since completion, return the final message; otherwise null
        }

        int daysIntoPack = Days.daysBetween(subscription.getStartDate(), LocalDate.now()).getDays();
        int messageIndex;
        int currentWeek = daysIntoPack / DAYS_IN_WEEK + 1;
        int daysIntoWeek = daysIntoPack % DAYS_IN_WEEK;

        if (subscription.getSubscriptionPack().getMessagesPerWeek() == 1) {
            messageIndex = currentWeek - 1;
        } else { // messagesPerWeek == 2
            if (daysIntoWeek > 0 && daysIntoWeek < 4) { // use this week's first message
                messageIndex = 2 * (currentWeek - 1);
            } else { // use this week's second message
                messageIndex = 2 * (currentWeek - 1) + 1;
            }
        }

        return subscription.getSubscriptionPack().getWeeklyMessages().get(messageIndex);
    }

}

