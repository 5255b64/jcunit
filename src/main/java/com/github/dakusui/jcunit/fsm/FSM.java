package com.github.dakusui.jcunit.fsm;

import java.util.List;

/**
 * An interface that represents a finite state machine's (FSM) specification.
 *
 * @param <SUT> A software under test.
 */
public interface FSM<SUT> {
  State<SUT> initialState();

  List<State<SUT>> states();

  List<Action<SUT>> actions();
}