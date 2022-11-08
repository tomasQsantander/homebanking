package com.santander.homebanking.services;

import com.santander.homebanking.dtos.ClientDTO;
import com.santander.homebanking.models.Account;
import com.santander.homebanking.models.CardColor;
import com.santander.homebanking.models.Client;
import com.santander.homebanking.repositories.AccountRepository;
import com.santander.homebanking.repositories.CardRepository;
import com.santander.homebanking.repositories.ClientRepository;
import com.santander.homebanking.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class ClientService {

    final int CANT_DIGITS = 3;
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CardRepository cardRepository;

    public List<ClientDTO> getClients() {
        return clientRepository.findAll().stream().map(ClientDTO::new).collect(toList());
    }

    public ClientDTO getClientByID(Long id){
        return clientRepository.findById(id).map(ClientDTO::new).orElse(null);
    }

    public ClientDTO getClientByEmail(String email){
        return clientRepository.findByEmail(email).map(ClientDTO::new).orElse(null);
    }

    public List<ClientDTO> getClientsByFirstName(String firstName){
        return clientRepository.findByFirstNameOrderByLastNameDesc(firstName).stream().map(ClientDTO::new).collect(Collectors.toList());
    }

    public Set<ClientDTO> getClientsHasTypeCards(String cardColor) throws IllegalArgumentException{
        try {
            return clientRepository.findByCardsColor(CardColor.valueOf(cardColor)).stream().map(ClientDTO::new).collect(Collectors.toSet());
        }catch (IllegalArgumentException e){
            throw new IllegalArgumentException();
        }
    }

    public Set<ClientDTO> getClientsLoansAmountGreater(Double amount){
        return clientRepository.findDistinctByClientLoansAmountGreaterThan(amount).stream().map(ClientDTO::new).collect(Collectors.toSet());
    }

    public Set<ClientDTO> getClientsLoansPaymentsGreater(Integer amount){
        return clientRepository.findDistinctByClientLoansPaymentsGreaterThan(amount).stream().map(ClientDTO::new).collect(Collectors.toSet());
    }

    public ResponseUtils register(
            String firstName, String lastName,
            String email, String password){

        ResponseUtils responseUtils = new ResponseUtils(true, 200, "client.register.success");

        if (clientRepository.findByEmail(email).orElse(null) !=  null) {
            responseUtils = new ResponseUtils(false, 400, "client.register.email.failure");
            return responseUtils;
        }

        String accountNumber = "VIN-" + new Random().nextInt((int)(Math.pow(10, CANT_DIGITS)));
        Account account = new Account(accountNumber, LocalDate.now(), 0.0);
        accountRepository.save(account);

        Client client = new Client(firstName, lastName, email, passwordEncoder.encode(password));
        client.addAccounts(account);
        clientRepository.save(client);


        return responseUtils;
    }

    public ClientDTO getCurrent(HttpSession session){
        return clientRepository.findByEmail(((Client)session.getAttribute("client")).getEmail()).map(ClientDTO::new).orElse(null);
    }

}
