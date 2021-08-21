package com.biniam.rabbitmqdemo;

import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
// The Receiver is a POJO that defines a method for receiving messages.
// When you register it to receive messages, you can name it anything you want.
public class Receiver {

    // For convenience, this POJO also has a CountDownLatch.
    // This lets it signal that the message has been received.
    // This is something you are not likely to implement in a production application.

    // A synchronization aid that allows one or more threads to wait
    // until a set of operations being performed in other threads completes.
    private CountDownLatch latch = new CountDownLatch(1);

    public void receiveMessage(String message) {
        System.out.println("Received <" + message + ">");
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }
}
