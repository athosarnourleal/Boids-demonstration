# Boids-demonstration
demonstração de uma IA basica que simula um cardume de peixes
## Funcionamento do algoritmo
algoritmo funciona com base em 4 forças que são computadas individualmente a partir da posição do proprio peixe e dos peixes a seu redor
### alligment
peixes tendem a se mover na mesma direção que os outros peixes proximos
### cohesion
peixes tendem a se mover na posição media dos peixes ao seu redor
### separation
peixes evitam se aproximar muito de outros peixes á seu redor
### border
peixes evitam sair do campo
## User Interface
são escondidas quando o botão direito do mouse não é pressionado, as barras podem ser arrastadas com o botao esquerdo para alterar constantes que influenciam a intensidade de cada força.
## Quadtree
para obter otimização, é usado o metodo quadtree, cuja funcionalidade pode ser demonstrada atravez das opções "show Quadtree" e "show QuadTree query demonstration"
