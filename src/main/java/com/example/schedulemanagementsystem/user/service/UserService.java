package com.example.schedulemanagementsystem.user.service;

import com.example.schedulemanagementsystem.user.dto.CreateUserRequest;
import com.example.schedulemanagementsystem.user.dto.CreateUserResponse;
import com.example.schedulemanagementsystem.user.dto.GetUserResponse;
import com.example.schedulemanagementsystem.user.dto.UpdateUserResponse;
import com.example.schedulemanagementsystem.user.entity.User;
import com.example.schedulemanagementsystem.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    //유저 생성
    @Transactional
    public CreateUserResponse save(CreateUserRequest request) {
        User user = new User(request.getName(), request.getEmail());
        User savedUser = userRepository.save(user);
        return new CreateUserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getCreatedAt(),
                savedUser.getModifiedAt()
        );
    }

    //유저 전체 조회
    @Transactional(readOnly = true)
    public List<GetUserResponse> findAll() {
        List<User> users = userRepository.findAll();
        List<GetUserResponse> dtos = new ArrayList<>();
        for (User user : users) {
            dtos.add(new GetUserResponse(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getCreatedAt(),
                    user.getModifiedAt()
            ));
        }
        return dtos;
    }

    //유저 단건 조회
    @Transactional(readOnly = true)
    public GetUserResponse findOne(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalStateException("존재하지 않는 유저입니다.")
        );
        return new GetUserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getModifiedAt()
        );
    }

    //유저 수정
    @Transactional
    public UpdateUserResponse update(Long userId, CreateUserRequest request) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalStateException("존재하지 않는 유저입니다.")
        );
        user.update(request.getName(), request.getEmail());

        //flush
        userRepository.flush();

        return new UpdateUserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getModifiedAt()
        );
    }

    //유저 삭제
    @Transactional
    public void delete(Long userId) {
        boolean exists = userRepository.existsById(userId);
        if (!exists) {
            throw new IllegalStateException("존재하지 않는 유저입니다.");
        }
        userRepository.deleteById(userId);
    }
}
