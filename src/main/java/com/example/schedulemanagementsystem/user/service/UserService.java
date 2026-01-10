package com.example.schedulemanagementsystem.user.service;

import com.example.schedulemanagementsystem.common.exception.ConflictException;
import com.example.schedulemanagementsystem.common.exception.ForbiddenException;
import com.example.schedulemanagementsystem.common.exception.NotFoundException;
import com.example.schedulemanagementsystem.config.PasswordEncoder;
import com.example.schedulemanagementsystem.user.dto.*;
import com.example.schedulemanagementsystem.user.entity.User;
import com.example.schedulemanagementsystem.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    //회원가입시 encode 생성
    //유저 생성(회원가입)
    @Transactional
    public SignupResponse save(@Valid SignupRequest request) {
        //이메일 중복체크
        boolean exists = userRepository.existsByEmail(request.getEmail());
        if (exists) {
            throw new ConflictException("이미 가입된 이메일입니다.");
        }

        //회원가입시 비밀번호 encode 생성
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(request.getName(), request.getEmail(), encodedPassword);
        User savedUser = userRepository.save(user);
        return new SignupResponse(
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
                () -> new NotFoundException("존재하지 않는 유저입니다.")
        );
        return new GetUserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getModifiedAt()
        );
    }

    //로그인한 유저와 대상 유저가 같을 때만
    //유저 수정
    @Transactional
    public UpdateUserResponse update(Long loginUserId, Long userId, @Valid UpdateUserRequest request) {
        //아이디 비교해서 제한
        if (!loginUserId.equals(userId)) {
            throw new ForbiddenException("본인만 수정할 수 있습니다.");
        }

        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("존재하지 않는 유저입니다.")
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

    //로그인한 유저와 대상 유저가 같을 때만
    //유저 삭제
    @Transactional
    public void delete(Long loginUserId, Long userId) {
        //아이디 비교해서 제한
        if (!loginUserId.equals(userId)) {
            throw new ForbiddenException("본인만 삭제할 수 있습니다.");
        }

        boolean exists = userRepository.existsById(userId);
        if (!exists) {
            throw new NotFoundException("존재하지 않는 유저입니다.");
        }
        userRepository.deleteById(userId);
    }

    //로그인
    @Transactional(readOnly = true)
    public SessionUser login(@Valid LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new NotFoundException("이메일 또는 비밀번호가 일치하지 않습니다.")
        );

        //비밀번호 확인시 matches 적용
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ForbiddenException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }
//        if (!user.getPassword().equals(request.getPassword())) {
//            throw new ForbiddenException("이메일 또는 비밀번호가 일치하지 않습니다.");
//        }

        return new SessionUser(user.getId(), user.getName(), user.getEmail());
    }
}
