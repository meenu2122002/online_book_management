package com.mukund.booknetwork.book;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedbackMapper {
    public Feedback toFeedback(FeedbackRequest request) {

        return Feedback.builder()
                .note(request.note())
                .comment(request.comment())
                .book(Book.builder().
                        id(request.bookId())
                        .build()
                )
                .build();
    }

    public FeedbackResponse toFeedbackResponse(Feedback feedback, Integer userId) {

        return FeedbackResponse.builder()
                .note(feedback.getNote())
                .comment(feedback.getComment())
                .ownFeedback(Objects.equals(userId,feedback.getCreatedBy()))
        .build();
    }
}
