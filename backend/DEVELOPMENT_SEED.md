# Povoamento manual de desenvolvimento

Este povoamento é um script SQL manual, exclusivo para o MySQL 8 local. Ele não é executado pelo Spring Boot e não acrescenta código contabilizado pelo JaCoCo.

O conjunto contém 2 usuários, 20 clientes pessoa física, 20 veículos (15 vendidos e 5 disponíveis) e 15 vendas. Foram ignorados PJ/CNPJ, vendedores, customizações, chassi, reservados, tabelas auxiliares e relação entre venda e usuário.

## Credenciais locais

| Perfil | E-mail | Senha |
|---|---|---|
| ADMIN | `admin.demo@sistemadegestao.local` | `DemoAdmin@123` |
| USER | `usuario.demo@sistemadegestao.local` | `DemoUser@123` |

O SQL contém somente hashes BCrypt gerados e validados com o `BCryptPasswordEncoder` do backend. As senhas acima são credenciais públicas de demonstração e nunca devem ser usadas em produção.

## Execução manual

Inicie o backend normalmente uma vez para o Hibernate criar ou atualizar o schema local. Encerre-o e conecte-se manualmente:

```powershell
mysql -u root -p concessionaria
```

Confira obrigatoriamente:

```sql
SELECT DATABASE();
```

Continue somente se o resultado for exatamente `concessionaria`. Não execute em `concessionaria_test`, conexão remota ou PostgreSQL de produção.

No mesmo cliente MySQL, execute o arquivo completo:

```sql
SOURCE C:/Users/luana/Documents/WORKSPACE/SistemaDeGestao/backend/src/main/resources/seed/development-seed.sql;
```

Não use `--force` nem execute trechos isolados. O script usa `JSON_TABLE`, disponível no MySQL 8.

## Transação e idempotência

O script cria o procedimento auxiliar `concessionaria.sp_development_seed_20260826`, chama-o uma vez e o remove. Ele existe apenas para disponibilizar `IF`, `SIGNAL`, `EXIT HANDLER` e `ROLLBACK`; nenhuma tabela, trigger ou migration é criada.

Um erro durante a carga aciona o handler, desfaz a transação e retorna `ABORTADO POR ERRO`. O handler absorve o erro depois do rollback para que a instrução seguinte remova o procedimento. Se a conexão for interrompida externamente antes do `DROP`, uma nova execução começa removendo o procedimento auxiliar remanescente.

Antes de inserir, são comparados os dois usuários (inclusive hash e perfil), os 20 CPFs e respectivos dados, os 20 veículos por todos os campos disponíveis e as 15 vendas com suas relações. Os resultados são:

- `JÁ APLICADO`: conjunto completo; nenhuma inserção;
- `ABORTADO POR CONFLITO`: conjunto parcial, duplicado ou incompatível; nenhuma inserção;
- `APLICADO`: banco sem dados coincidentes; carga integral confirmada.

O script não apaga, trunca ou atualiza dados existentes. IDs são gerados pelo `AUTO_INCREMENT`; as FKs são recuperadas por CPF e pela combinação completa dos campos do veículo.

Referências preservadas: clientes 1–20; notas 1–15; veículos vendidos das notas 1–15; disponíveis `novo-27`, `novo-29`, `usado-26`, `usado-28` e `usado-30`. A relação real continua sendo `numero_nota N -> cliente N -> veículo da nota N`.
