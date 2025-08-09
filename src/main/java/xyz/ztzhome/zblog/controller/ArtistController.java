package xyz.ztzhome.zblog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.ztzhome.zblog.entity.DTO.AddArtistDTO;
import xyz.ztzhome.zblog.entity.response.ResponseMessage;
import xyz.ztzhome.zblog.service.impl.ArtistServiceImpl;

@RequestMapping("/api/artists")
@RestController
public class ArtistController {

    @Autowired
    ArtistServiceImpl artistService;
    @GetMapping("/{id}")
    public ResponseMessage getArtiById(@PathVariable("id") Integer id){
        return artistService.getArtistById(id);
    }

    @PostMapping
    public ResponseMessage addArtist(@RequestBody AddArtistDTO addArtistDTO){
        return artistService.addArtist(addArtistDTO);
    }
}
