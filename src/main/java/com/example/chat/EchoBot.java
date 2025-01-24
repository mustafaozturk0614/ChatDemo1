// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.example.chat;

import com.example.chat.dialogs.DialogBot;
import com.microsoft.bot.builder.ActivityHandler;
import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.schema.ChannelAccount;
import com.example.chat.dialogs.MainDialog;
import com.example.chat.dialogs.Dialog;
import com.example.chat.dialogs.DialogHelper;
import com.microsoft.bot.builder.ConversationState;
import com.microsoft.bot.builder.UserState;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * This class implements the functionality of the Bot.
 *
 * <p>
 * This is where application specific logic for interacting with the users would be added. For this
 * sample, the {@link #onMessageActivity(TurnContext)} echos the text back to the user. The {@link
 * #onMembersAdded(List, TurnContext)} will send a greeting to new conversation participants.
 * </p>
 */
public class EchoBot extends DialogBot<MainDialog> {
    public EchoBot(ConversationState conversationState, UserState userState, MainDialog dialog) {
        super(conversationState, userState, dialog);
    }

    @Override
    protected CompletableFuture<Void> onMessageActivity(TurnContext turnContext) {
        return DialogHelper.run(dialog, turnContext, 
            conversationState.createProperty("DialogState"));
    }

    @Override
    protected CompletableFuture<Void> onMembersAdded(List<ChannelAccount> membersAdded, TurnContext turnContext) {
        return membersAdded.stream()
            .filter(member -> !member.getId().equals(turnContext.getActivity().getRecipient().getId()))
            .findFirst()
            .map(member -> DialogHelper.run(dialog, turnContext, 
                conversationState.createProperty("DialogState")))
            .orElse(CompletableFuture.completedFuture(null));
    }
}
