# Smart Payment Descriptor for java

The **SmartPayment** project is a full java implementation of [Smart Payment Descriptor](http://qr-platba.cz) which is a standard for QR code payments on mobile devices in Czech Republic. If you're interested in full specification, please go to http://qr-platba.cz website.

## Library Features

### License

The library is licensed under [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0). It means that it can be freely used for non-commercial and also for commercial projects. For more information look for full license agreement.

The SPD standard is also patent free.

### Already implemented features

* Parsing and full validation of SPD code
* Full IBAN validation
* Czech account number validation
* Czech model object for payment

### Missing features

* SpaydWriter class is not implemented yet
* CRC32 validation

### Usage

The basic usage is very simple:

```

    SpaydReader<Payment> reader = SpaydReader.from(SpaydConfig.defaultConfig())
    final ReaderResult<Payment> result = mSpayd.readFromSpayd(validCode1);

    if (result.isSuccess()) {
        // do stuff with result.getPayment()
    } else {
        // handle errors in result.getErrors() or just show general message like "This is not va;id QR code with payment"
    }
```