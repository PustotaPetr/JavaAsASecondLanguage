package io.github.javaasasecondlanguage.billing;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * fixed implementation of billing service
 * Money are safe here
 */
@Controller
@RequestMapping("billing")
public class BillingResource {
    private Map<String, Account> userToMoney = new ConcurrentHashMap<>();

    /**
     * curl -XPOST localhost:8080/billing/addUser -d "user=sasha&money=100000"
     * curl -XPOST localhost:8080/billing/addUser -d "user=sergey&money=100000"
     */
    @RequestMapping(
            path = "addUser",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> addUser(@RequestParam("user") String user,
                                          @RequestParam("money") Integer money) {

        if (user == null || money == null) {
            return ResponseEntity.badRequest().body("");
        }

        Account newAccount = new Account(user, money);
        userToMoney.put(user, newAccount);

        return ResponseEntity.ok("Successfully created user [" + user + "] with money "
                + userToMoney.get(user) + "\n");
    }

    /**
     * curl -XPOST localhost:8080/billing/sendMoney -d "from=sergey&to=sasha&money=1"
     */
    @RequestMapping(
            path = "sendMoney",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> sendMoney(@RequestParam("from") String fromUser,
                                            @RequestParam("to") String toUser,
                                            @RequestParam("money") Integer money) {
        if (fromUser == null || toUser == null || money == null) {
            return ResponseEntity.badRequest().body("");
        }

        if (!userToMoney.containsKey(fromUser) || !userToMoney.containsKey(toUser)) {
            return ResponseEntity.badRequest().body("No such user\n");
        }

        ArrayList<Account> users = new ArrayList<>();
        users.add(userToMoney.get(fromUser));
        users.add(userToMoney.get((toUser)));
        Collections.sort(users, new Comparator<Account>() {
            @Override
            public int compare(Account o1, Account o2) {
                return o1.name.compareTo(o2.name);
            }
        });
        synchronized (users.get(0)) {
            synchronized (users.get(1)) {
                if (userToMoney.get(fromUser).money < money) {
                    return ResponseEntity.badRequest().body("Not enough money to send\n");
                }
                userToMoney.get(fromUser).money -= money;
                userToMoney.get(toUser).money += money;
            }
        }
        return ResponseEntity.ok("Send success\n");
    }

    /**
     * curl localhost:8080/billing/stat
     */
    @RequestMapping(
            path = "stat",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getStat() {
        ResponseEntity<String> result;
        synchronized (userToMoney) {
            result = ResponseEntity.ok(userToMoney + "\n");
        }
        return result;
    }

    class Account {
        final String name;
        Integer money;

        Account(String name, Integer money) {
            this.name = name;
            this.money = money;
        }

        @Override
        public String toString() {
            return "Account{" + "name='" + name + '\''
                    + ", money=" + money + '}';
        }
    }
}
