package com.mukund.booknetwork.book;


import com.mukund.booknetwork.book.exception.OperationNotPermittedException;
import com.mukund.booknetwork.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedbackService {

private final BookRepository bookRepository;
private final FeedbackMapper feedbackMapper;

private final FeedbackRepository feedbackRepository;



    public Integer save(FeedbackRequest request, Authentication connectedUser) {
        System.out.println("vheenu");
        System.out.println(request.bookId());
        System.out.println(request.comment());

        Book  book=bookRepository.findById(request.bookId())
                .orElseThrow(() -> new EntityNotFoundException("Book for Feedback is not Found"));

       User user=(User)connectedUser.getPrincipal();
       if(Objects.equals(user.getId(),book.getOwner().getId())){
           throw new OperationNotPermittedException("Owner of Book cannot provide feedback to his book");
       }
       if(book.isArchived() || !book.isShareable()){
           throw new OperationNotPermittedException("Book is Either not Shareable or is Archived");
       }


       Feedback feedback=feedbackMapper.toFeedback(request);
       return feedbackRepository.save(feedback).getId();





    }



@Transactional
    public PageResponse<FeedbackResponse> getFeedback(int page, int size, Integer bookId,Authentication connectedUser) {
        Book  book=bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book for Feedback is not Found"));
        if(book.isArchived() || !book.isShareable()){
            throw new OperationNotPermittedException("Book is Either not Shareable or is Archived");
        }
        User user=(User)connectedUser.getPrincipal();
        Pageable pageable= PageRequest.of(page,size, Sort.by("decending"));
        Page<Feedback> feedbackResponsePage=feedbackRepository.findAllFeedbacks(pageable,bookId);
        List<FeedbackResponse>feedbackResponses=feedbackResponsePage.stream()
                .map(feedback -> feedbackMapper.toFeedbackResponse(feedback,user.getId()))

        .toList();

return new PageResponse<>(
        feedbackResponses

        ,feedbackResponsePage.getNumber(),
        feedbackResponsePage.getSize()
        ,feedbackResponsePage.getTotalElements()
        , feedbackResponsePage.getTotalPages()
        ,feedbackResponsePage.isFirst(),
        feedbackResponsePage.isLast()

                );

    }
}
