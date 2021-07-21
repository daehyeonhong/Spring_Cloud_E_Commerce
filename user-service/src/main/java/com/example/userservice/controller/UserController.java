package com.example.userservice.controller;

import com.example.userservice.dto.UserDto;
import com.example.userservice.repository.UserEntity;
import com.example.userservice.service.UserService;
import com.example.userservice.vo.Greeting;
import com.example.userservice.vo.RequestUser;
import com.example.userservice.vo.ResponseUser;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {

    private final Environment environment;
    private final UserService userService;
    private final Greeting greet;

    @GetMapping("/health_check")
    public String status() {
//        return String.format("It's Working in User Service on PORT: %s", environment.getProperty("local.server.port"));
        return String.format("It's Working in User Service \n" +
                        ": port(local.server.port):%s\n" +
                        ": port(server.port):%s\n" +
                        ": token secret:%s\n" +
                        ": token expiration time:%s"
                , environment.getProperty("local.server.port")
                , environment.getProperty("server.port")
                , environment.getProperty("token.secret")
                , environment.getProperty("token.expiration_time"));
    }

    @GetMapping("/welcome")
    public String welcome() {
//        return environment.getProperty("greeting.message");
        return greet.getMessage();
    }

    @PostMapping("/users")
    public ResponseEntity<ResponseUser> createUser(@RequestBody RequestUser user) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = modelMapper.map(user, UserDto.class);

        ResponseUser responseUser = modelMapper.map(userService.createUser(userDto), ResponseUser.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }

    @GetMapping("/users")
    public ResponseEntity<List<ResponseUser>> getUsers() {
        Iterable<UserEntity> userEntities = userService.getUserByAll();

        List<ResponseUser> users = new ArrayList<>();
        userEntities.forEach(value -> users.add(new ModelMapper().map(value, ResponseUser.class)));

        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ResponseUser> getUser(@PathVariable("userId") String userId) {
        UserDto userDto = userService.getUserByUserId(userId);

        ResponseUser responseUser = new ModelMapper().map(userDto, ResponseUser.class);

        return ResponseEntity.status(HttpStatus.OK).body(responseUser);
    }

}