USE vaadinstart;

INSERT INTO kategoria (nimi, kuvaus, alv_prosentti, hylly_sijainti, palautus_aika_paivina) VALUES 
('Älypuhelimet', 'Modernit älypuhelimet ja tarvikkeet', 25.5, 'Hyllyväli A1', 14),
('Tietokoneet', 'Kannettavat ja pöytäkoneet', 25.5, 'Hyllyväli A2', 30),
('Tabletit', 'Viihde- ja työkäyttöön soveltuvat tabletit', 25.5, 'Hyllyväli A3', 14),
('Audio', 'Kuulokkeet, kaiuttimet ja vahvistimet', 25.5, 'Hyllyväli A4', 14),
('Komponentit', 'Prosessorit, näytönohjaimet ja emolevyt', 25.5, 'Hyllyväli B1', 30),
('Oheislaitteet', 'Hiiret, näppäimistöt ja näytöt', 25.5, 'Hyllyväli B2', 14),
('Pelaaminen', 'Konsolit ja pelitarvikkeet', 25.5, 'Hyllyväli B3', 14),
('Kodinkoneet', 'Pienkoneet ja keittiötarvikkeet', 25.5, 'Hyllyväli B4', 30),
('Verkkolaitteet', 'Reitittimet ja kytkimet', 25.5, 'Hyllyväli C1', 14),
('Älykoti', 'Valaistus ja turvajärjestelmät', 25.5, 'Hyllyväli C2', 14);

INSERT INTO ominaisuus (nimi) VALUES 
('Vedenkestävä'),
('Langaton'),
('Älylaite'),
('Bluetooth'),
('Langaton lataus'),
('Älykoti');



