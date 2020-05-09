#  BlockCV - Blockchain project using Hyperledger Fabric  #
BlockCV is an open-source project that aims to create a distributed system based on a Permissioned Blockchain technology suitable for posting certified resumes and looking for job offers.

## Requirements

- `curl`, `docker`, `docker-compose`
- Node.js [only if you want to use blockchain-explorer]

### Extra reqs: http://hyperledger-fabric.readthedocs.io/en/release-1.1/prereqs.html

## Usage
- Download binaries: software\blockcv-blockchain\network\download-binaries.sh
- Download docker images: software\blockcv-blockchain\network\download-images.sh
- Start the network: software\blockcv-blockchain\network\start.sh

- Build the client `software\blockcv-client` with `Maven`
- For example, this is a suitable run configuration:
	`mvn:compile`
	`jetty:run`







