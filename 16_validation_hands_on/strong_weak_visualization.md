Didn't end up using these, but didn't want to erase them

---

# Typical validation

Usually converting from a weaker/broader type to a stronger/narrower one

Narrow = less freedom = less wiggle room

---

# Visually

```
           ----------------------------------
          |              Weaker/             |
          |               Looser             |
          |                                  |
          |        ------------------        |
          |       |    Stronger/     |       |
          |       |     Narrower     |       |
          |       |                  |       |
          |       |      :)          |  :(   |
          | :(    |                  |       |
          |        ------------------        |
          |                                  |
           ----------------------------------
```

> Usually converting from a weaker type to a stronger one

Many weak values don't map to a strong one

---

# Examples

```
   Weak  |  Strong | Examples       ----------------------------------
   Type  |   Type  |               |              Weaker/             |
 ----------------------------      |               Looser             |
 String  |   Int   |  "130"        |                                  |
         |         |  "Boban"      |        ------------------        |
         |         |               |       |    Stronger/     |       |
 String  |LocalDate| "2020-05-03"  |       |     Narrower     |       |
         |         | "hi"          |       |                  |       |
         |         |               |       |                  |       |
 Int     | Seconds |  50           |       |                  |       |
         |         |  75           |        ------------------        |
         |         |               |                                  |
         |         |                ----------------------------------
```
