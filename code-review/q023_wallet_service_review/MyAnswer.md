# My Answer — Q023

## Issues Found

### 1. [Category] — [short title]
1. getWallet is just doing get, It needs to do get or throw an exception
2. deposit needs to be @Transactional
3. Constructor inejection of attributes is missing
4. WalletRepository needs to use @Version for race conditions

### 2. ...
