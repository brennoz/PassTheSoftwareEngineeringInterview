# Q016 — My Answer: Fund Transfer Service

Write each issue below using the format from question.md.

    1. Both are missing constructor with attributes injection
    2. transfer method in the TransactionService have to transactional
    3. after this transactionRepository.save(tx); I would probably use oubox-pattern saving the transaction event in another table
        as unpublished. I would have polling process which will look periodically for all unpublished transaction and send them to a kafka
        topic. i would remove this auditService.log(tx);   // synchronous call to external audit API the consumers from the auditservice would be
        consuming data from the topic and log them asynchronously
    4. I would probably use @Version annotation for this: AccountRepository accountRepository;
    5. this tx.getStatus() == "COMPLETED" must be equals or equalsIgnoreCase
    6. this block Account account = accountRepository.findById(tx.getFromAccountId()).get();
                tx.setAccountName(account.getName()); is unnecessary remove it        

