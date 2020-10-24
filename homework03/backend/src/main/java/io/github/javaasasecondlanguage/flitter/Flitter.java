package io.github.javaasasecondlanguage.flitter;

import com.google.gson.Gson;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@RestController
public class Flitter {

    Map<String, List<NamedFlit>> flits = new ConcurrentHashMap<>();
    Queue<NamedFlit> lastFlits = new ConcurrentLinkedQueue<>();

    @RequestMapping(path = "clear", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public Map clear() {
        Users.users.clear();
        flits.clear();
        lastFlits.clear();
        return Collections.singletonMap("", "");
    }

    @PostMapping(path = "/flit/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map> addFlit(@RequestBody String flitStr) {
        Gson gson = new Gson();
        InputFlit inputFlit = gson.fromJson(flitStr, InputFlit.class);

        if (Users.users.containsKey(inputFlit.userToken)) {
            //add
            NamedFlit namedFlit = new NamedFlit(
                    Users.users.get(inputFlit.userToken),
                    inputFlit.content);

            if (flits.containsKey(namedFlit.userName)) {
                flits.get(namedFlit.userName).add(namedFlit);
            } else {
                //no flits yet;
                List<NamedFlit> userFlit = new ArrayList<>();
                userFlit.add(namedFlit);
                flits.put(namedFlit.userName, userFlit);
            }

            lastFlits.add(namedFlit);
            if (lastFlits.size() > 10) {
                lastFlits.remove();
            }
            return ResponseEntity.ok(Collections.singletonMap("", ""));

        } else {   //error: no user
            HashMap<String, String> response = new HashMap<>();
            response.put("data", null);
            response.put("errorMessage", "User not found");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @RequestMapping(path = "/flit/discover",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Map discover() {
        NamedFlit[] flitArray = new NamedFlit[lastFlits.size()];
        lastFlits.toArray(flitArray);
        return Collections.singletonMap("data", flitArray);
    }

    @RequestMapping(path = "/flit/list/{username}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map> getUserFlit(@PathVariable String username) {
        if (Users.users.containsValue(username)) {
            List<NamedFlit> listFlits = flits.getOrDefault(username, new ArrayList<>());
            NamedFlit[] namedFlits = new NamedFlit[listFlits.size()];
            listFlits.toArray(namedFlits);
            return ResponseEntity.ok(Collections.singletonMap("data", namedFlits));

        } else {
            HashMap<String, String> response = new HashMap<>();
            response.put("data", null);
            response.put("errorMessage", "User not found");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @RequestMapping(path = "/flit/list/feed/{usertoken}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map> getFeedFlit(@PathVariable String usertoken) {
        if (Users.users.containsKey(usertoken)) {
            List<String> publishiers = Subscriber.publishers.get(usertoken);
            List<NamedFlit> feedFlits = new ArrayList<>();
            for (int i = 0; i < publishiers.size(); i++) {
                feedFlits.addAll(flits.get(publishiers.get(i)));

            }
            return ResponseEntity.ok(Collections.singletonMap("data", feedFlits.toArray()));

        } else {
            HashMap<String, String> response = new HashMap<>();
            response.put("data", null);
            response.put("errorMessage", "User not found");
            return ResponseEntity.badRequest().body(response);
        }
    }


    class InputFlit {
        public String userToken;
        public String content;

        public InputFlit(String userToken, String content) {
            this.userToken = userToken;
            this.content = content;
        }
    }

    class NamedFlit {
        public String userName;
        public String content;

        public NamedFlit(String userName, String content) {
            this.userName = userName;
            this.content = content;
        }
    }


}
