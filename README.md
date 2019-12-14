# Aplicativo Android desenvolvido para a disciplina de Programação para Dispositivos Móveis. 

No primeiro acesso, o usuário deverá fazer SignUp para se cadastrar no sistema. Em seguida, terá acesso ao sistema como usuário(vendedor) comum (sem privilégio de administrador) e poderá gerenciar ordens de serviço e equipamantos, mas não poderá cadastrar um novo cliente; 
Como regra, somente o admin, terá acesso ao cadastramento de Clientes na navbar(esta verificação é feita na 'OsActivity' linhas 71 e 72);
exemplo de usuário administrador já cadastrado: usuario: 'admin@admin.com', senha: '123456'; para definir um novo administrador, deverá ser feita alteração manualmente no nó User no firebase, definindo função 'admin' para o usuário desejado.
Os passos de utilização após o Login são:
1. Cadastrar(se administrador) ou selecionar um cliente;
2. Na navbar, Cadastrar o equipamento desejado e selecionar um cliente ao qual o equipamento pertence;
3. Na navbar, Cadastrar uma OS clicando em OS, selecione um equipamento para vincular à OS;
4. Alterações nos registros poderão ser feitas à medida em que a OS sofrer alterações.

--> Controladores:
OsActivity - IMPLEMENTADO;
EquipamentoActivity - IMPLEMENTADO;
ClientesActivity - IMPLEMENTADO - obs.: Trecho Clientes foi Adaptado do Professor;
LoginActivity - IMPLEMENTADO;
--> Desafios: 
Notificações - IMPLEMENTADO;
SignUP - IMPLEMENTADO;
