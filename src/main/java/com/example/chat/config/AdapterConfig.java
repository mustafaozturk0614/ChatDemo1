package com.example.chat.config;

import com.microsoft.bot.integration.BotFrameworkHttpAdapter;
import com.microsoft.bot.builder.AutoSaveStateMiddleware;
import com.microsoft.bot.builder.ConversationState;
import com.microsoft.bot.builder.UserState;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AdapterConfig {

    @Bean
    @Primary
    public BotFrameworkHttpAdapter getBotFrameworkHttpAdapter(ConversationState conversationState, UserState userState, com.microsoft.bot.integration.Configuration configuration) {
        BotFrameworkHttpAdapter adapter = new BotFrameworkHttpAdapter(configuration);
        // AutoSaveStateMiddleware, her turn sonunda ConversationState ve UserState'in kaydedilmesini sağlar.
        adapter.use(new AutoSaveStateMiddleware(conversationState, userState));
        return adapter;
    }
} 