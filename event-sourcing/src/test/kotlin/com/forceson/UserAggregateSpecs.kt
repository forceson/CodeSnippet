package com.forceson

import autoparams.AutoSource
import autoparams.customization.Customization
import autoparams.mockito.MockitoCustomizer
import com.forceson.command.ChangePassword
import com.forceson.command.CreateUser
import com.forceson.command.RaiseUnknownEvent
import com.forceson.event.PasswordHashChanged
import com.forceson.event.UnknownEvent
import com.forceson.event.UserCreated
import com.forceson.messaging.Message
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatException
import org.assertj.core.api.Assertions.assertThatNoException
import org.junit.jupiter.params.ParameterizedTest

class UserAggregateSpecs {

    @ParameterizedTest
    @AutoSource
    fun canHandle_returns_true_with_valid_data(
        eventStore: InMemoryEventStore,
        message: CommandWrapper<CreateUser>
    ) {
        val sut = UserAggregate(eventStore)
        val actual = sut.canHandle(message.get())
        assertThat(actual).isTrue()
    }

    @ParameterizedTest
    @AutoSource
    fun canHandle_returns_false_with_non_stream_command(
        eventStore: InMemoryEventStore,
        message: CommandWrapper<Message.Event<String>>
    ) {
        val sut = UserAggregate(eventStore)
        val actual = sut.canHandle(message.get())
        assertThat(actual).isFalse()
    }

    @ParameterizedTest
    @AutoSource
    fun canHandle_returns_false_with_unknown_command_payload(
        eventStore: InMemoryEventStore,
        message: CommandWrapper<String>
    ) {
        val sut = UserAggregate(eventStore)
        val actual = sut.canHandle(message.get())
        assertThat(actual).isFalse()
    }

    @ParameterizedTest
    @AutoSource
    fun handle_collects_raised_event(
        eventStore: InMemoryEventStore,
        message: CommandWrapper<CreateUser>
    ) {
        val sut = UserAggregate(eventStore)
        val streamCommand = message.get() as Message.Command<*>
        val streamId = streamCommand.streamId
        val command = streamCommand.payload as CreateUser

        sut.handle(message.get())

        val actual = eventStore.queryEvents(User::class.java, streamId)
        assertThat(actual)
            .usingRecursiveComparison()
            .isEqualTo(
                listOf(
                    UserCreated(command.username),
                    PasswordHashChanged(command.password)
                )
            )
    }

    @ParameterizedTest
    @AutoSource
    fun handle_correctly_sets_version(
        eventStore: InMemoryEventStore,
        userCreated: UserCreated,
        passwordHashChanged: PasswordHashChanged,
        message: CommandWrapper<ChangePassword>
    ) {
        eventStore.collectEvents(User::class.java, message.streamId, listOf(userCreated, passwordHashChanged))

        val sut = UserAggregate(eventStore)

        assertThatNoException().isThrownBy { sut.handle(message.get()) }
    }

    @ParameterizedTest
    @AutoSource
    fun handle_correctly_sets_context_data(
        message: CommandWrapper<ChangePassword>
    ) {
        val eventStore = mockk<EventStore>()
        every {
            eventStore.queryEvents(any(), any(), any())
        } returns listOf()
        every {
            eventStore.collectEvents(
                any(),
                message.streamId,
                any(),
                any()
            )
        } just runs
        val sut = UserAggregate(eventStore)

        sut.handle(message.get())

        verify {
            eventStore.collectEvents(
                any(),
                message.streamId,
                any(),
                any()
            )
        }
    }

    @ParameterizedTest
    @AutoSource
    @Customization(MockitoCustomizer::class)
    fun handle_fails_if_command_payload_is_unknown(
        sut: UserAggregate,
        message: CommandWrapper<String>
    ) {
        assertThatException()
            .isThrownBy { sut.handle(message.get()) }
            .withMessageContaining("Unsupported command payload type")
            .withMessageContaining(String::class.java.name)
    }

    @ParameterizedTest
    @AutoSource
    @Customization(MockitoCustomizer::class)
    fun handle_fails_if_unknown_event_produced(
        sut: UserAggregate,
        message: CommandWrapper<RaiseUnknownEvent>
    ) {
        assertThatException()
            .isThrownBy { sut.handle(message.get()) }
            .withMessageContaining("Event that cannot be handled was produced")
            .withMessageContaining(UnknownEvent::class.java.name)
    }
}