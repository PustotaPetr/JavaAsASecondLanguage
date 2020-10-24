package io.github.javaasasecondlanguage.flitter;

import com.google.gson.Gson;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class Subscriber {
    public static Map<String, List<String>> subscriptions = new ConcurrentHashMap<>();
    public static Map<String, List<String>> publishers = new ConcurrentHashMap<>();

    @RequestMapping(path = "/subscribe", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map> subscribeHim(@RequestBody() String jsonString) {
        System.out.println("subscribeHim : " + jsonString);

        if (addDeleteSubscriptions(jsonString, true)) {
            return ResponseEntity.ok(Collections.singletonMap("", ""));
        } else {
            //token is absent
            //TODO return error status
            HashMap<String, String> response = new HashMap<>();
            response.put("data", null);
            response.put("errorMessage", "User not found");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @RequestMapping(method = RequestMethod.POST, path = "/unsubscribe",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map> unsubscribeHim(@RequestBody() String jsonString) {
        System.out.println("unsubscribeHim : " + jsonString);

        if (addDeleteSubscriptions(jsonString, false)) {
            return ResponseEntity.ok(Collections.singletonMap("", ""));
        } else {
            //token is absent
            //TODO return error status
            HashMap<String, String> response = new HashMap<>();
            response.put("data", null);
            response.put("errorMessage", "User not found");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @RequestMapping(path = "/subscribers/list/{userToken}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map> getSubscribers(@PathVariable String userToken) {
        System.out.println("getSubscribers : " + userToken);
        System.out.println(Arrays.toString(subscriptions.keySet().toArray()));

        if (Users.users.containsKey(userToken)) {
            return ResponseEntity.ok(
                    Collections.singletonMap("data", subscriptions.getOrDefault(userToken, new ArrayList<>()).
                            toArray()));
        } else {
            //token is absent
            //TODO return error status
            HashMap<String, String> response = new HashMap<>();
            response.put("data", null);
            response.put("errorMessage", "User not found");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @RequestMapping(path = "/publishers/list/{userToken}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map> getPublishers(@PathVariable String userToken) {
        System.out.println("getPublishers : " + userToken);
        System.out.println(Arrays.toString(publishers.keySet().toArray()));


        if (Users.users.containsKey(userToken)) {
            return ResponseEntity.ok(
                    Collections.singletonMap("data", publishers.getOrDefault(userToken, new ArrayList<>()).
                            toArray()));
        } else {
            //token is absent
            //TODO return error status
            HashMap<String, String> response = new HashMap<>();
            response.put("data", null);
            response.put("errorMessage", "User not found");
            return ResponseEntity.badRequest().body(response);
        }
    }

    private boolean addDeleteSubscriptions(String jsonStr, boolean add) {
        Gson gson = new Gson();
        SubscribeJson subscribeJson = gson.fromJson(jsonStr, SubscribeJson.class);
        String myToken = subscribeJson.subscriberToken;
        String hisToken = "";

        if (Users.users.containsKey(myToken)) {
            String myName = Users.users.get(myToken);
            String hisName = subscribeJson.publisherName;
            for (String uid : Users.users.keySet()) {
                System.out.println("uid : " + uid + "name : " + Users.users.get(uid));
                if (Users.users.get(uid).equals(hisName)) {
                    hisToken = uid;
                    break;
                }
            }

            System.out.println("myToken : " + myToken);
            System.out.println("hisToken : " + hisToken);
            System.out.println("myName : " + myName);
            System.out.println("hisName : " + hisName);

            //token have, we can continue;
            if (subscriptions.containsKey(hisToken)) {
                if (add) {
                    subscriptions.get(hisToken).add(myName);
                } else {
                    subscriptions.get(hisToken).remove(myName);
                }
            } else {
                if (add) {
                    List<String> newSubscription = new ArrayList<>();
                    newSubscription.add(myName);
                    subscriptions.put(hisToken, newSubscription);
                }
                //id need delete and subscription absent - then nothing to do
            }

            if (publishers.containsKey(myToken)) {
                if (add) {
                    publishers.get(myToken).add(hisName);
                } else {
                    publishers.get(myToken).remove(hisName);
                }
            } else {
                if (add) {
                    List<String> newSubscription = new ArrayList<>();
                    newSubscription.add(hisName);
                    publishers.put(myToken, newSubscription);
                }
            }
            return true;
        } else {
            //token is absent
            return false;
        }
    }

    class SubscribeJson {
        public String subscriberToken;
        public String publisherName;

        public SubscribeJson(String subscriberToken, String publisherName) {
            this.subscriberToken = subscriberToken;
            this.publisherName = publisherName;
        }
    }
}
