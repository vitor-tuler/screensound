package br.com.alura.screensound.principal;

import br.com.alura.screensound.model.Artista;
import br.com.alura.screensound.model.Categoria;
import br.com.alura.screensound.model.Musica;
import br.com.alura.screensound.repository.ArtistaRepository;
import br.com.alura.screensound.service.ConsultaChatGPT;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Principal {
    private Scanner leitura = new Scanner(System.in);

    private ArtistaRepository repository;

    public Principal(ArtistaRepository repository) {this.repository = repository;}

    public void exibeMenu() {
        var opcao = -1;
        while(opcao != 0){
            var menu = """
                    1 - Cadastrar Artista
                    2 - Cadastrar musicas
                    3 - Listar artistas
                    4 - Listar musicas por artista
                    5 - Pesquisar um artista
                    
                    0 - Sair
                    """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();
            switch (opcao){
                case 1:
                    cadastraArtistas();
                    break;
                case 2:
                    cadastraMusica();
                    break;
                case 3:
                    listaMusicas();
                    break;
                case 4:
                    BuscarMusicasPorArtista();
                    break;
                case 5:
                    buscaArtista();
                    break;
                case 0:
                    System.out.println("Saindo ...");
                default:
                    System.out.println("Opcao invalida");
            }
        }
    }

    private void cadastraArtistas() {
        var cadastrarNovo = "S";

        while (cadastrarNovo.equalsIgnoreCase("s")) {
            System.out.println("Informe o nome desse artista: ");
            var nome = leitura.nextLine();
            System.out.println("Informe o tipo desse artista: (solo, dupla ou banda)");
            var tipo = leitura.nextLine();
            Categoria categoria = Categoria.valueOf(tipo.toUpperCase());
            Artista artista = new Artista(nome, categoria);
            repository.save(artista);
            System.out.println("Cadastrar novo artista? (S/N)");
            cadastrarNovo = leitura.nextLine();
        }
    }

    private void cadastraMusica() {
        System.out.println("Cadastrar música de que artista? ");
        var nome = leitura.nextLine();
        Optional<Artista> artista = repository.findByNomeArtisticoContainingIgnoreCase(nome);
        if (artista.isPresent()) {
            System.out.println("Informe o título da música: ");
            var nomeMusica = leitura.nextLine();
            Musica musica = new Musica(nomeMusica);
            musica.setArtista(artista.get());
            artista.get().getMusicas().add(musica);
            repository.save(artista.get());
        } else {
            System.out.println("Artista não encontrado");
        }
    }

    private void listaMusicas() {
        List<Artista> artistas = repository.findAll();
        artistas.forEach(a -> a.getMusicas().forEach(System.out::println));
    }

    private void BuscarMusicasPorArtista() {
        System.out.println("Buscar músicas de que artista? ");
        var nome = leitura.nextLine();
        List<Musica> musicas = repository.buscaMusicasPorArtista(nome);
        musicas.forEach(System.out::println);
    }

    private void buscaArtista() {
        System.out.println("Pesquisar dados sobre qual artista? ");
        var nome = leitura.nextLine();
        var resposta = ConsultaChatGPT.obterInformacao(nome);
        System.out.println(resposta.trim());
    }
}
