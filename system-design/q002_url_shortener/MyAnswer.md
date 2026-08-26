# My Answer — URL Shortener

<!-- Write your design here. Sections below are suggested structure. -->

A service that maps a short opaque code (e.g. short.ly/aB3xKz) to an original long URL and redirects visitors to it. Read-heavy (100:1 read/write typical), latency-critical on redirect, and requires globally unique code generation at scale without coordination overhead.

## Short Code Generation

The code will be name of the machine like A1 plus a generated id like 0001
so the result will be A10001 encoded in Base62 to avoid +/ and characters

## Data Model

I will store everything in a NoSql database (not necessary a relational) like DynamoDB?

## Redirect Flow (301 vs 302)

I don't know

## Caching Strategy

I will use Redis. Read through if shortUrl is in the cache I will return the va lue from the cache otherwise I will query the DB

## Scale & Bottlenecks
I can scale the DB via Sharding
