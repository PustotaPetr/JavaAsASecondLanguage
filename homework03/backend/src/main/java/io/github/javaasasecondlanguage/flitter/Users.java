package io.github.javaasasecondlanguage.flitter;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("user")
public class Users {
    public static Map<String, String> users = new ConcurrentHashMap<>();

    @RequestMapping(path = "list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map list() {
        Set<String> uids = users.keySet();
        String[] userNames = new String[uids.size()];
        int i = 0;
        for (String uid : uids) {
            userNames[i++] = users.get(uid);
        }
        return Collections.singletonMap("data", userNames);
    }

    @RequestMapping(path = "register", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map> register(@RequestBody() String userStr) {
        Gson gson = new Gson();
        InputUser user = gson.fromJson(userStr, InputUser.class);
        user.userToken = UUID.randomUUID().toString();

        if (users.containsValue(user.userName)) {
            HashMap<String, String> response = new HashMap<>();
            response.put("data", null);
            response.put("errorMessage", "This name is already taken");

            return ResponseEntity.badRequest().body(response);
        } else {
            users.put(user.userToken, user.userName);
            return ResponseEntity.ok(Collections.singletonMap("data", user));
        }
    }

    private class InputUser {
        public String userName;
        public String userToken;

        public InputUser(String userName) {
            super();
            this.userName = userName;
            this.userToken = "";
        }
    }
}
