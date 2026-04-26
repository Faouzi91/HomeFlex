package com.homeflex.features.property.domain;

import com.homeflex.core.exception.IllegalStateTransitionException;
import com.homeflex.features.property.domain.enums.BookingStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BookingStateMachineTest {

    @Test
    void testDraftTransitions() {
        assertThat(BookingStateMachine.canTransition(BookingStatus.DRAFT, BookingStatus.PAYMENT_PENDING)).isTrue();
        assertThat(BookingStateMachine.canTransition(BookingStatus.DRAFT, BookingStatus.PENDING_APPROVAL)).isTrue();
        assertThat(BookingStateMachine.canTransition(BookingStatus.DRAFT, BookingStatus.CANCELLED)).isTrue();

        assertThatThrownBy(() -> BookingStateMachine.transition(BookingStatus.DRAFT, BookingStatus.COMPLETED))
                .isInstanceOf(IllegalStateTransitionException.class);
    }

    @Test
    void testPaymentPendingTransitions() {
        assertThat(BookingStateMachine.canTransition(BookingStatus.PAYMENT_PENDING, BookingStatus.PENDING_APPROVAL)).isTrue();
        assertThat(BookingStateMachine.canTransition(BookingStatus.PAYMENT_PENDING, BookingStatus.PAYMENT_FAILED)).isTrue();
        assertThat(BookingStateMachine.canTransition(BookingStatus.PAYMENT_PENDING, BookingStatus.CANCELLED)).isTrue();

        assertThatThrownBy(() -> BookingStateMachine.transition(BookingStatus.PAYMENT_PENDING, BookingStatus.ACTIVE))
                .isInstanceOf(IllegalStateTransitionException.class);
    }
    
    @Test
    void testPaymentFailedTransitions() {
        assertThat(BookingStateMachine.canTransition(BookingStatus.PAYMENT_FAILED, BookingStatus.PAYMENT_PENDING)).isTrue();
        assertThat(BookingStateMachine.canTransition(BookingStatus.PAYMENT_FAILED, BookingStatus.CANCELLED)).isTrue();

        assertThatThrownBy(() -> BookingStateMachine.transition(BookingStatus.PAYMENT_FAILED, BookingStatus.PENDING_APPROVAL))
                .isInstanceOf(IllegalStateTransitionException.class);
    }

    @Test
    void testPendingApprovalTransitions() {
        assertThat(BookingStateMachine.canTransition(BookingStatus.PENDING_APPROVAL, BookingStatus.APPROVED)).isTrue();
        assertThat(BookingStateMachine.canTransition(BookingStatus.PENDING_APPROVAL, BookingStatus.REJECTED)).isTrue();
        assertThat(BookingStateMachine.canTransition(BookingStatus.PENDING_APPROVAL, BookingStatus.CANCELLED)).isTrue();

        assertThatThrownBy(() -> BookingStateMachine.transition(BookingStatus.PENDING_APPROVAL, BookingStatus.ACTIVE))
                .isInstanceOf(IllegalStateTransitionException.class);
    }

    @Test
    void testApprovedTransitions() {
        assertThat(BookingStateMachine.canTransition(BookingStatus.APPROVED, BookingStatus.ACTIVE)).isTrue();
        assertThat(BookingStateMachine.canTransition(BookingStatus.APPROVED, BookingStatus.CANCELLED)).isTrue();
        assertThat(BookingStateMachine.canTransition(BookingStatus.APPROVED, BookingStatus.PENDING_MODIFICATION)).isTrue();

        assertThatThrownBy(() -> BookingStateMachine.transition(BookingStatus.APPROVED, BookingStatus.COMPLETED))
                .isInstanceOf(IllegalStateTransitionException.class);
    }
    
    @Test
    void testPendingModificationTransitions() {
        assertThat(BookingStateMachine.canTransition(BookingStatus.PENDING_MODIFICATION, BookingStatus.APPROVED)).isTrue();

        assertThatThrownBy(() -> BookingStateMachine.transition(BookingStatus.PENDING_MODIFICATION, BookingStatus.CANCELLED))
                .isInstanceOf(IllegalStateTransitionException.class);
    }

    @Test
    void testActiveTransitions() {
        assertThat(BookingStateMachine.canTransition(BookingStatus.ACTIVE, BookingStatus.COMPLETED)).isTrue();

        assertThatThrownBy(() -> BookingStateMachine.transition(BookingStatus.ACTIVE, BookingStatus.CANCELLED))
                .isInstanceOf(IllegalStateTransitionException.class);
    }

    @Test
    void testTerminalStates() {
        BookingStatus[] statuses = BookingStatus.values();
        
        for (BookingStatus target : statuses) {
            assertThat(BookingStateMachine.canTransition(BookingStatus.REJECTED, target)).isFalse();
            assertThat(BookingStateMachine.canTransition(BookingStatus.CANCELLED, target)).isFalse();
            assertThat(BookingStateMachine.canTransition(BookingStatus.COMPLETED, target)).isFalse();
        }
    }
}
